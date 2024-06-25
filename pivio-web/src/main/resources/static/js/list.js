function list(field, value) {

    var searchQuery = encodeURI(`{"match":{"${field}":"'${value}'"}}`),
        url = `${apiserver_js}/document?query=${searchQuery}&fields=id,name`;

    $.ajax({
        url: url,
        dataType: 'json',
        cache: false,
        success: function listDocuments(documents) {
            documents.pivioValue = value;
            documents.pivioField = field;
            var source = $("#list-template").html(),
                template = Handlebars.compile(source);
            $('#list').append(template(documents));
        }
    });

};