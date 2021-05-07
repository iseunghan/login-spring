$(function () {
    $('#btn-login').click(function () {
        login();
    });
});

function formValidate() {
    var $id = $('#input-login-username');
    var $pw = $('#input-login-password');
    console.log($id.val() + ', ' + $pw.val());

    if ($id.val() === '') {
        $id.removeClass('is-valid');
        $id.addClass('is-invalid');
    } else {
        $id.removeClass('is-invalid');
        $id.addClass('is-valid');
    }

    if ($pw.val() === '') {
        $pw.removeClass('is-valid');
        $pw.addClass('is-invalid');
    } else {
        $pw.removeClass('is-invalid');
        $pw.addClass('is-valid');
    }

    if ($id.val() === '' || $pw.val() === '') {
        return false;
    }
    return true;
}

function login() {
    if (formValidate()) {
        var $username = $('#input-login-username').val();
        var $password = $('#input-login-password').val();
        $.ajax({
            type: 'GET',
            url: 'http://localhost:8080/members/login/' + $username,
            contentType: 'application/json',

            success: function (result) {
                if (result.password === $password) {
                    console.log('success');
                    alert('Login Success!');
                } else {
                    console.log('invalid - password!!')
                    alert('Login Failed : invalid - password');
                }
            },
            error: function (x, e) {
                if (x.status == 404) {
                    alert('404 Not Found : 아이디를 다시 확인해주세요');
                    console.log(x);
                    console.log(e);
                }
            }
        });
    }
}