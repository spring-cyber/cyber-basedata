<#if removeIdList?has_content>
delete from ${tableName} where
    <#list removeIdList as Id>
        ${Id.pkCode} = ${Id.pkValue}
        <#if Id?has_next> or<#else >;</#if>
    </#list>

</#if>

<#if addDataList?has_content>
insert into ${tableName}
    (${columnNames})
    values
    <#list addDataList as data>
        (${data})
        <#if data?has_next>,</#if>
    </#list>
</#if>

