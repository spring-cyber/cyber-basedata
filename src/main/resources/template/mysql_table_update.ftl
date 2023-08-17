<#if comment?has_content>
alter table ${tableName}
    comment '${comment}';
</#if>

<#if engine?has_content>
alter table ${tableName}
    engine = ${engine};
</#if>

<#if collate?has_content>
alter table ${tableName}
    collate = ${collate};
</#if>

<#if changeColumnList?has_content>
alter table ${tableName}
    <#list changeColumnList as column>
    ${column.way} ${column.code} ${column.type}<#if column.length!=0>(${column.length})</#if> <#if column.defaultVal!=''>default ${column.defaultVal}</#if> <#if column.autoIncrement=1>auto_increment</#if> <#if column.notNull=0>null<#else >not null</#if> comment '${column.name}' <#if column.primaryKey=1>primary key</#if><#if column?has_next>,<#else >;</#if>
    </#list>
</#if>



