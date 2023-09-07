create table ${tableName}
(
<#list columnList as column>
    ${column.code} ${column.type}<#if column.length?has_content&& column.length?is_number&& column.length != 0>(${column.length})</#if> <#if column.defaultVal?has_content>default ${column.defaultVal}</#if> <#if column.autoIncrement=1>auto_increment</#if> <#if column.notNull=0>null<#else >not null</#if> comment '${column.name}' <#if column.primaryKey=1>primary key</#if><#if column?has_next || fkList?has_content>,</#if>
</#list>

<#if fkList?has_content>
    <#list fkList as fk>
    constraint `${fk.name}`
        foreign key (`${fk.columnCode}`) references ${fk.clTableCode} (${fk.clColumnCode})
            on update ${fk.update} on delete ${fk.delete}<#if fk?has_next>,</#if>
    </#list>
</#if>
)
<#if comment?has_content> comment '${comment}'</#if> <#if engine?has_content>engine = `${engine}`</#if>
<#if collate?has_content> collate = ${collate}</#if>
    <#if autoIncrementVal?has_content >auto_increment = ${autoIncrementVal}</#if>;

<#if indexList?has_content>
    <#list indexList as index>
create ${index.type} ${index.name}
    on ${tableName} (${index.columnCode} <#if index.sortord?has_content>${index.sortord}</#if>) <#if index.description?has_content>comment '${index.description}'</#if>;

    </#list>
</#if>
