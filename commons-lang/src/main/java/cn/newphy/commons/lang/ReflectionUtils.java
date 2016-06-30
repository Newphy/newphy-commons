package cn.newphy.commons.lang;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;

public class ReflectionUtils {


    /**
     * 根据已有泛型数组新建新的数组.
     *
     * @param array
     * @param length
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> T[] newArray(T[] array, int length) {
        if (array == null) {
            return null;
        }
        Class<T> clazz = (Class<T>)array.getClass().getComponentType();
        return newArray(clazz, length);
    }


    /**
     * 根据类型新建数组.
     *
     * @param clazz
     * @param length
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<T> clazz, int length) {
        return (T[])Array.newInstance(clazz, length);
    }


    /**
     * 获得类的泛型定义.
     *
     * <p>
     *     ex. public class List<String> {}
     *     return String.class
     * </p>
     *
     * @param clazz
     * @param index
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static Class getSuperClassGenericType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
             return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 获得类的泛型定义.
     *
     * <p>
     * ex. public class List<String> {} return String.class
     * </p>
     *
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperClassGenericType(final Class<?> clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    /**
     * 获得Field的泛型定义类
     * <p>
     * ex. public class Exam { private List<String> list; } 对于 "list" Field,
     * 调用方法后将返回String.class
     * </p>
     *
     * @param field
     * @param index
     * @return
     */
    public static Class<?> getFieldGenericType(Field field, int index) {
        if (field == null) {
            return null;
        }

        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            return null;
        }

        ParameterizedType pType = (ParameterizedType) type;
        Type[] argumentTypes = pType.getActualTypeArguments();
        if (index > argumentTypes.length - 1) {
            throw new IllegalArgumentException("index[" + index
                    + "] is out of field[" + field + "] generic type array");
        }

        return (Class<?>) argumentTypes[index];
    }
    
    
    /**
     * 获得方法的参数信息
     * 
     * @param method
     * @return
     */
    public static MethodParameter[] getMethodParameters(Method method) {
    	LocalVariableTableParameterNameDiscoverer paramNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    	Class<?>[] parameterTypes = method.getParameterTypes();
    	MethodParameter[] mps = new MethodParameter[method.getParameterTypes().length];
    	if(!ArrayUtils.isEmpty(parameterTypes)) {
    		for (int i = 0; i < mps.length; i++) {
				mps[i] = new MethodParameter(method, i);
				mps[i].initParameterNameDiscovery(paramNameDiscoverer);
			}
    	}
    	return mps;
    }
    
    /**
     * 获得方法的参数名称
     * @param method
     * @return
     */
    public static String[] getMethodParameterNames(final Method method) {
    	 final String[] paramNames = new String[method.getParameterTypes().length];
         final String n = method.getDeclaringClass().getName();
         ClassReader cr = null;
         try {
             cr = new ClassReader(n);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
         cr.accept(new ClassVisitor(Opcodes.ASM4) {
             @Override
             public MethodVisitor visitMethod(final int access,
                     final String name, final String desc,
                     final String signature, final String[] exceptions) {
                 final  org.springframework.asm.Type[] args = org.springframework.asm.Type.getArgumentTypes(desc);
                 // 方法名相同并且参数个数相同
                 if (!name.equals(method.getName())
                         || !sameType(args, method.getParameterTypes())) {
                     return super.visitMethod(access, name, desc, signature,
                             exceptions);
                 }
                 MethodVisitor v = super.visitMethod(access, name, desc,
                         signature, exceptions);
                 return new MethodVisitor(Opcodes.ASM4, v) {
                     @Override
                     public void visitLocalVariable(String name, String desc,
                             String signature, Label start, Label end, int index) {
                         int i = index - 1;
                         // 如果是静态方法，则第一就是参数
                         // 如果不是静态方法，则第一个是"this"，然后才是方法的参数
                         if (Modifier.isStatic(method.getModifiers())) {
                             i = index;
                         }
                         if (i >= 0 && i < paramNames.length) {
                             paramNames[i] = name;
                         }
                         super.visitLocalVariable(name, desc, signature, start,
                                 end, index);
                     }
  
                 };
             }
         }, 0);
         return paramNames;
    }
    
    /**
     * 
     * <p>
     * 比较参数类型是否一致
     * </p>
     * 
     * @param types
     *            asm的类型({@link Type})
     * @param clazzes
     *            java 类型({@link Class})
     * @return
     */
    private static boolean sameType(org.springframework.asm.Type[] types, Class<?>[] clazzes) {
        // 个数不同
        if (types.length != clazzes.length) {
            return false;
        }
 
        for (int i = 0; i < types.length; i++) {
            if (!org.springframework.asm.Type.getType(clazzes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }
}
