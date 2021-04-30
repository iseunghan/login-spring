$(function () {
    console.log('ready to docs')

    $('#btn-submit-join').click(function (){
        join();
    });

    $('#btn-username-valid').click(function () {
        var $username = $('#input-join-username').val();

        $.ajax({
            type: 'GET',
            url: 'http://localhost:8080/members/login/' + $username,
            contentType: 'application/json',

            success: function (result) {
                $('.username-not-duplicate').hide();
                $('.username-duplicate').show();
            },
            error: function (x, e) {
                if (x.status == 404) {
                    $('.username-duplicate').hide();
                    $('.username-not-duplicate').show();
                }
            }
        });
    });
});

function formValidate() {
    var $name = $('#input-join-name');
    var $username = $('#input-join-username');
    var $pw = $('#input-join-pw');
    console.log($username.val() + ', ' + $pw.val() + ', ' + $name.val());

    if ($name.val() === '') {
        $name.removeClass('is-valid');
        $name.addClass('is-invalid');
    } else {
        $name.removeClass('is-invalid');
        $name.addClass('is-valid');
    }

    if ($pw.val() === '') {
        $pw.removeClass('is-valid');
        $pw.addClass('is-invalid');
    } else {
        $pw.removeClass('is-invalid');
        $pw.addClass('is-valid');
    }

    if ($username.val() === '') {
        $username.removeClass('is-valid');
        $username.addClass('is-invalid');
    } else {
        $username.removeClass('is-invalid');
        $username.addClass('is-valid');
    }

    if ($username.val() === '' || $pw.val() === '' || $name.val() === '') {
        return false;
    }
    return true;
}

function join() {
    console.log('join!');
    if (formValidate()) {
        console.log('ajax 통신 시도..');

        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/members',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                username: $('#input-join-username').val(),
                password: $('#input-join-pw').val(),
                name: $('#input-join-name').val(),
            }),

            success: function (result) {
                console.log('success');
                console.log(result);
                alert("🥳회원가입이 완료되었습니다!🥳\nID: " + result.username + "\nPW: " + result.password);
                location.href = ('http://localhost:8080/');
            },
            error: function (x, e) {
                if (x.status == 400) {
                    alert('Username 중복확인을 해주세요 🥺');
                }
            }
        });
        return true;
    }
    return false;
}