export enum {{name}} {
{{#enumValues}}
    {{symbol}} = '{{value}}'{{#iter.hasNext}},{{/iter.hasNext}}
{{/enumValues}}
}
