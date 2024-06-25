$(function () {
    $("#resultarea").hide();

    $("#submit").click(function () {
        $("#resultarea").hide();

        var fieldInput = $("#fields").val().replace(/ /g, '');
        var queryInput = $("#query").val();

        var query = "?";

        if (fieldInput != "") {
            query = query + "fields=" + fieldInput;
        }
        if (queryInput != "") {
            query = query + "&query=" + queryInput;
        }
        var url = apiserver_js + "/document" + encodeURI(query);

        $.ajax({
            url: url,
            dataType: 'json',
            cache: false,
            success: function showResult(result) {
                $("#result").empty();
                $("#result").append(syntaxHighlight(JSON.stringify(result, undefined, 4)));
                $("#serverquery").empty();
                $("#serverquery").append(url);
                $("#resultarea").show();
            },
            statusCode: {
                400: function () {
                    $("#serverquery").empty();
                    var source = $("#changes").html();
                    var template = Handlebars.compile(source);
                    $('#crtx-search').append(template({"error": "Invalid search request."}));
                }
            }
        });

        function syntaxHighlight(json) {
            json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
            return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
                var cls = 'number';
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) {
                        cls = 'key';
                    } else {
                        cls = 'string';
                    }
                } else if (/true|false/.test(match)) {
                    cls = 'boolean';
                } else if (/null/.test(match)) {
                    cls = 'null';
                }
                return '<span class="' + cls + '">' + match + '</span>';
            });
        }


    });
});

