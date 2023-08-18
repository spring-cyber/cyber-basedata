create table ${tableName}
(
<#list columnList as column>
    ${column.code} ${column.type}<#if column.length!=0>(${column.length})</#if> <#if column.defaultVal!=''>default ${column.defaultVal}</#if> <#if column.autoIncrement=1>auto_increment</#if> <#if column.notNull=0>null<#else >not null</#if> comment '${column.name}' <#if column.primaryKey=1>primary key</#if><#if column?has_next || fkList?has_content>,</#if>
</#list>

<#if fkList?has_content>
    <#list fkList as fk>
    constraint `${fk.name}`
        foreign key (`${fk.columnCode}`) references ${fk.clTableCode} (${fk.clColumnCode})
            on update ${fk.update} on delete ${fk.delete}<#if fk?has_next>,</#if>
    </#list>
</#if>
)
    comment '${comment}' engine = `${engine}`
                         collate = ${collate}
    <#if autoIncrementVal?has_content >auto_increment = ${autoIncrementVal}</#if>;

<#if indexList?has_content>
    <#list indexList as index>
create ${index.type} ${index.name}
    on ${tableName} (${index.columnCode} <#if index.sortord?has_content>${index.sortord}</#if>) comment '${index.description}';

    </#list>
</#if>
