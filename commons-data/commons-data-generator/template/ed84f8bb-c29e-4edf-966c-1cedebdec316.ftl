<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${namespace}">
	<parameterMap  id="${sourceShortId}ParameterMap"  type="${entityType.name}"/>	

	<sql id="sqlColumnList">
		<trim suffixOverrides=",">
			<#list resultMappings as resultMapping>
			${resultMapping.column},
			</#list>
		</trim>
	</sql>  

	<sql id="sqlTColumnList">
		<trim suffixOverrides=",">
			<#list resultMappings as resultMapping>
			t.${resultMapping.column},
			</#list>
    	</trim>
	</sql>
	
	<insert id="save" parameterMap="${sourceShortId}ParameterMap" <#if idMapping ??>useGeneratedKeys="true" keyProperty="${idMapping.property}"</#if> >
		INSERT INTO ${tableName}
		<trim prefix="(" suffix=")" suffixOverrides=",">
			<#list insertMappings as resultMapping>
				<if test=" null != ${resultMapping.property} <#if resultMapping.javaType.name == "java.lang.String">and ''!= ${resultMapping.property}  </#if>">
				${resultMapping.column},
				</if>
			</#list>
		</trim>
		<trim prefix="VALUES(" suffix=")" suffixOverrides=",">
			<#list resultMappings as resultMapping>
				<if test=" null != ${resultMapping.property} <#if resultMapping.javaType.name == "java.lang.String">and ''!= ${resultMapping.property}  </#if>">
					#${r'{'}${resultMapping.property}},
				</if>
			</#list>
		</trim>
	</insert>

	<insert id="batchSave" parameterType="java.util.Collection">
		INSERT INTO ${tableName} 
		<trim prefix="(" suffix=")" >
			<include refid="sqlColumnList" />
		</trim>
		VALUES
		<foreach item="entity" index="index" collection="list" separator=",">
			<trim prefix="(" suffix=")" suffixOverrides=",">
				<#list insertMappings as resultMapping> 
					#${r'{'}entity.${resultMapping.property},jdbcType=${resultMapping.jdbcType}},
				</#list>
			</trim>
		</foreach>
	</insert>
	
	<update id="update" parameterMap="${sourceShortId}ParameterMap">
		UPDATE ${tableName}
			<set>
				<trim suffixOverrides=",">
					<#list updateMappings as resultMapping>
					<#if idMapping.column != resultMapping.column>
						${resultMapping.column} = #${r'{'}${resultMapping.property}},
					</#if>
					</#list>
				</trim>
			</set>
		<where>
			${idMapping.column} = #${r'{'}${idMapping.property}}
		</where>
	</update>
	
	<update id="updateOptimistic" parameterMap="${sourceShortId}ParameterMap">
		UPDATE ${tableName}
			<set>
				<trim suffixOverrides=",">
					<#list updateMappings as resultMapping>
						<#if idMapping.column != resultMapping.column>
							<#if versionMapping??>
								<#if versionMapping.column != resultMapping.column>
										${resultMapping.column} = #${r'{'}${resultMapping.property}},
								</#if>
							<#else>
										${resultMapping.column} = #${r'{'}${resultMapping.property}},
							</#if>

					</#if>
					</#list>
					<#if versionMapping??>
						${versionMapping.column} = ${versionMapping.column} + 1,
					</#if>
				</trim>
			</set>
		<where>
			${idMapping.column} = #${r'{'}${idMapping.property}} 	<#if versionMapping??> AND ${versionMapping.column} = #${r'{'}${versionMapping.property}}</#if>
		</where>
	</update>
	
	<update id="batchUpdate" parameterType="java.util.Collection">
		<foreach collection="list" item="item" index="index" open="" close="" separator=";">
			UPDATE ${tableName}
				<set>
					<trim suffixOverrides=",">
						<#list updateMappings as resultMapping>
						<#if idMapping.column != resultMapping.column>
							<if test="item.${resultMapping.property}!=null <#if resultMapping.javaType.name == "java.lang.String">and ''!= item.${resultMapping.property}  </#if>">
								${resultMapping.column} = #${r'{'}item.${resultMapping.property},jdbcType=${resultMapping.jdbcType}},
							</if>
						</#if>
						</#list>
					</trim>
				</set>
				WHERE ${idMapping.column} = #${r'{'}item.${idMapping.property}}
		</foreach>
	</update>
	
	<delete id="delete" >
		DELETE FROM ${tableName}
		<where>
			${idMapping.column} = #${r'{'}${idMapping.property}}
		</where>
	</delete>	
	
	<delete id="batchDelete" parameterType="java.util.Collection">
		DELETE FROM ${tableName}
		<where>
			<foreach item="entity" index="index" collection="list"  open="(" separator="or" close=") ">
				${idMapping.column}  = #${r'{'}entity.${idMapping.property}}
			</foreach>
		</where>
	</delete>
	
	<delete id="deleteById" >
		DELETE FROM ${tableName}
		<where>
			${idMapping.column} = #${r'{'}0}
		</where>
	</delete>	

	<select id="get"  resultMap="ResultMap">
		SELECT  <include refid="sqlColumnList" />
		FROM ${tableName} WHERE ${idMapping.column} = #${r'{'}0}
 	</select>
 	
	<select id="getBy"  resultMap="ResultMap">
		SELECT  <include refid="sqlTColumnList" />
		FROM ${tableName} t WHERE 1=1
		<include refid="sqlCondition" />
		<include refid="sqlOrderBy" />
 	</select> 
 	
 	<select id="getOneBy"  resultMap="ResultMap">
		SELECT  <include refid="sqlTColumnList" />
		FROM ${tableName} t WHERE 1=1
		<include refid="sqlCondition" /> LIMIT 1
 	</select> 		
 	
	<select id="getAll" resultMap="ResultMap"  >
			SELECT <include refid="sqlColumnList" /> FROM ${tableName} t <include refid="sqlOrderBy" />
	</select> 	
    
	<sql id="sqlCondition">
		<#list resultMappings as resultMapping>
		<if test=" null != ${resultMapping.property} <#if resultMapping.javaType.name == "java.lang.String">and ''!= ${resultMapping.property}  </#if>">
			AND	t.${resultMapping.column} = #${r'{'}${resultMapping.property}}
		</if>
		</#list>
	</sql>  

	<sql id="sqlOrderBy">
		<foreach collection="__orders" item="order" index="index" open="ORDER BY " close="" separator=", ">
			${r'${order.column} ${order.direction}'}
		</foreach>
	</sql>  	   


	<select id="query" resultMap="ResultMap"  >
			SELECT <include refid="sqlColumnList" /> FROM ${tableName} t WHERE 1=1
			<include refid="sqlCondition" />
	</select>

	<select id="count" resultType="int" >
			SELECT count(1) FROM ${tableName} t WHERE 1=1
			<include refid="sqlCondition" />
	</select>

</mapper>