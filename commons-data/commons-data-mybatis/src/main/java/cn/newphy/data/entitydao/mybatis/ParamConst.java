package cn.newphy.data.entitydao.mybatis;

public interface ParamConst {
	
	// args array index
    public final static int IDX_MAPPED_STATEMENT = 0;
    public final static int IDX_PARAMETER_OBJECT = 1;
    public final static int IDX_ROWBOUNDS= 2;

	// parameter name
    public static final String PARAM_NAME_PAGE = "__pageable";
    public static final String PARAM_NAME_ENTITY = "__entity";
	public static final String PARAM_NAME_ORDERS = "__orders";
    
	// placeholder
    public static final String PLACEHOLDER_OFFSET = "__pageable.offset";
    public static final String PLACEHOLDER_LIMIT = "__pageable.pageSize";
}
