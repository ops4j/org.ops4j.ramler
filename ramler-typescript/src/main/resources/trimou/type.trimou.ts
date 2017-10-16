{{#type}}
{{#properties}}
{{#propertyContext}}
{{#if tsFile}}{{>import}}{{/if}}
{{/propertyContext}}    
{{/properties}}

export class {{name}} {
{{#properties}}
{{#propertyContext}}
{{>property}}    
{{/propertyContext}}    
{{/properties}}
}
{{/type}}

