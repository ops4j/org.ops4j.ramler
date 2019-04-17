    {{name}}({{>parameters}}): RestResponse<{{returnType}}> {
        return this.httpClient.{{httpMethod}}<{{returnType}}>({{url}}{{body}}{{options}}).toPromise();
    }

