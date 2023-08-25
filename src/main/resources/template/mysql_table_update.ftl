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
        <#if column.way='drop'>
    drop column ${column.code}<#if column?has_next>,<#else >;</#if>
        <#else >
    ${column.way} <#if column.oldCode?has_content>${column.oldCode}</#if> <#if column.code?has_content>${column.code}</#if> ${column.type}<#if column.length!=0>(${column.length})</#if> <#if column.defaultVal?has_content>default ${column.defaultVal}</#if> <#if column.autoIncrement=1>auto_increment</#if> <#if column.notNull=0>null<#else >not null</#if> comment '${column.name}' <#if column.primaryKey=1>primary key</#if><#if column?has_next>,<#else >;</#if>
        </#if>
   </#list>
</#if>

<#if changeFkList?has_content>
alter table ${tableName}
    <#list changeFkList as fk>
        <#if fk.way='drop'>
    drop foreign key `${fk.name}`<#if fk?has_next>,<#else >;</#if>
        <#else >
    ${fk.way} constraint `${fk.name}`
              foreign key (`${fk.columnCode}`) references ${fk.clTableCode} (${fk.clColumnCode})
                   on update ${fk.update} on delete ${fk.delete}<#if fk?has_next>,<#else >;</#if>
        </#if>
   </#list>
</#if>

<#if changeIndexList?has_content>
    <#list changeIndexList as index>
        <#if index.way='drop'>
    drop index ${tableName} on `${index.name}`<#if index?has_next>,<#else >;</#if>
        <#else >
    ${index.way} ${index.type} ${index.name}
            on ${tableName} (${index.columnCode} <#if index.sortord?has_content>${index.sortord}</#if>) <#if index.description?has_content>comment '${index.description}'</#if>;
        </#if>
   </#list>
</#if>



