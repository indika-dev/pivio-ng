$(function () {
    $('#searchbox').keyup(function () {
        var searchTerm = $(this).val().toLowerCase();
        $(`.card[data-meta*='${searchTerm}']`).show();
        $('.card').not(`[data-meta*='${searchTerm}']`).hide();

        if (searchTerm.trim() == '') {
            $(".card").show();
        }
        countArtifacts();
    });

    countArtifacts();
});


function countArtifacts() {
    var n = $('a.ui.card:not([style*="display: none"])').length;
    $("#artifactsheader").text("Matching Artifacts (" + n + ")");
};
