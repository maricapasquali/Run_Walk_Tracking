window.onload = function () {

    const ui = SwaggerUIBundle({
        url: "/docs/endpoints-docs.yaml",
        dom_id: '#swagger-ui',
        deepLinking: true,
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
       ],
       plugins: [
        SwaggerUIBundle.plugins.DownloadUrl
       ],
        layout: "StandaloneLayout"
    });

    window.ui = ui;
};
