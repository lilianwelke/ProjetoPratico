function init() {
    document.querySelector("body").addEventListener("load", verificarTempo);
    document.querySelector(".cadastrar").addEventListener("click", criarConta);
    document.querySelector(".logar").addEventListener("click", verificarLogin);
    document.querySelector(".desLogar").addEventListener("click", deslogar);
    document.querySelector(".minhaConta").addEventListener("click", irMinhaConta);

    validarLogon();
}

function verificarTempo() {
    if (localStorage.logon)
    {
        var cliente = JSON.parse(window.localStorage.getItem('logon'));
        var dataLogin = new Date(cliente["cliente"][0]["data"]);
        dataLogin.setDate(dataLogin.getDate() + 7);

        if (dataLogin <= new Date())
        {
            deslogar();
        }
    }
}

function irInicio() {
    document.querySelector("#NOME").value = "";
    document.querySelector("#EMAIL").value = "";
    document.querySelector("#SENHAPX").value = "";
    document.querySelector("#confirmSenha").value = "";

    document.querySelector("#CGC").value = "";
    document.querySelector("#CEP").value = "";
    document.querySelector("#ENDERECO").value = "";
    document.querySelector("#NUMERO").value = "";
    document.querySelector("#BAIRRO").value = "";
    document.querySelector("#FONE").value = "";
    document.querySelector("#CELULAR").value = "";
    document.querySelector("#NCIDADE1").value = "";
    document.querySelector("#NUF1").value = "";
    document.querySelector("#COMPLEMENTO").value = "";

    document.querySelector("#EMAILL").value = "";
    document.querySelector("#SENHAL").value = "";

    document.querySelector(".olaCliente").innerText = "";

    setInvisible();
    document.querySelector("header").style.display = "block";
    document.querySelector("nav").style.display = "block";
    document.querySelector("article").style.display = "block";
    document.querySelector("footer").style.display = "block";
    document.querySelector(".fundoInicio").style.display = "block";
    setVisible(".fundoInicio");
}


function criarConta() {
    setVisiblePrincipais();
    setInvisible();
    document.querySelector("section").style.display = "block";
    document.querySelector(".divCriarConta").style.display = "block";
    setVisible("section");
    setVisible(".divCriarConta");

    document.querySelector("#imgHeaderSec").addEventListener("click", irInicio);
    document.querySelector("#h1HeaderSec").addEventListener("click", irInicio);
    document.querySelector("#h2HeaderSec").addEventListener("click", irInicio);

    document.querySelector("#criarConta").addEventListener("click", cadastrarCliente);

}

function cadastrarCliente() {
    if (document.querySelector("#NOME").value.trim() !== "" && document.querySelector("#EMAIL").value.trim() !== "" &&
            document.querySelector("#SENHAPX").value.trim() !== "" && document.querySelector("#confirmSenha").value.trim() !== "")
    {
        if (document.querySelector("#SENHAPX").value === document.querySelector("#confirmSenha").value)
        {
            requisicaoHTTP("projetoPratico", "cliente", "inserirCliente", continuarConta, alert, "&NOME=" + document.querySelector("#NOME").value
                    + "&EMAIL=" + document.querySelector("#EMAIL").value + "&SENHAPX=" + document.querySelector("#SENHAPX").value);
        } else {
            alert("As senhas informadas não são iguais!");
            document.querySelector("#SENHAPX").value = "";
            document.querySelector("#confirmSenha").value = "";
        }
    } else {
        alert("Preencha todos os campos obrigatórios!");
    }
}

function continuarConta(cliente) {
    document.querySelector("#NOME").value = "";
    document.querySelector("#EMAIL").value = "";
    document.querySelector("#SENHAPX").value = "";
    document.querySelector("#confirmSenha").value = "";

    setInvisible();
    document.querySelector("section").style.display = "block";
    document.querySelector(".divContinuarConta").style.display = "block";
    setVisible("section");
    setVisible(".divContinuarConta");

    var nomeCliente = cliente["NOME"].split(" ");

    document.querySelector(".olaCliente").innerText = "Bem vinda(o) " + nomeCliente[0] + "! Precisamos de mais algumas informações";

    document.querySelector("#salvarDadosConta").addEventListener("click", continuarCadastro);
    document.querySelector("#salvarDadosConta").dados = cliente;

    document.querySelector("#CEP").addEventListener("blur", verificarCep);
}

function continuarCadastro() {
    if (document.querySelector("#CGC").value.trim() !== "" && document.querySelector("#CEP").value.trim() !== "" &&
            document.querySelector("#ENDERECO").value.trim() !== "" && document.querySelector("#NUMERO").value.trim() !== ""
            && document.querySelector("#BAIRRO").value.trim() !== "" && document.querySelector("#CELULAR").value.trim() !== "")
    {
        var botao = document.querySelector("#salvarDadosConta").dados;
        requisicaoHTTP("projetoPratico", "cliente", "inserirClienteEnd", finalizarConta, alert, "&CCLIFOR=" + botao["CCLIFOR"]
                + "&NOME=" + botao["NOME"] + "&EMAIL=" + botao["EMAIL"] + "&CPF=" + document.querySelector("#CGC").value
                + "&CEP=" + document.querySelector("#CEP").value + "&ENDERECO=" + document.querySelector("#ENDERECO").value
                + "&NUMERO=" + document.querySelector("#NUMERO").value + "&BAIRRO=" + document.querySelector("#BAIRRO").value
                + "&FONE=" + document.querySelector("#FONE").value + "&CELULAR=" + document.querySelector("#CELULAR").value
                + "&NCIDADE1=" + document.querySelector("#NCIDADE1").value + "&NUF1=" + document.querySelector("#NUF1").value
                + "&COMPLEMENTO=" + document.querySelector("#COMPLEMENTO").value);
    } else {
        alert("Preencha todos os campos obrigatórios!");
    }
}

function verificarCep() {
    var http = new XMLHttpRequest();
    if (document.querySelector("#CEP").value.length === 8) {
        http.open('GET', 'https://viacep.com.br/ws/' + document.querySelector("#CEP").value + '/json/', true);
        http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        http.addEventListener('load', function () {
            if (http.status === 200) {
                var dados = JSON.parse(http.response);
                if (dados.erro) {
                    document.querySelector("#NCIDADE1").value = "";
                    document.querySelector("#NUF1").value = "";
                    alert("CEP inválido! O CEP informado não existe!");
                } else {
                    document.querySelector("#NCIDADE1").value = dados.localidade;
                    document.querySelector("#NUF1").value = dados.uf;
                }
            }
        });
        http.send(null);
    } else if (document.querySelector("#CEP").value.length > 0) {
        document.querySelector("#NCIDADE1").value = "";
        document.querySelector("#NUF1").value = "";
        alert("CEP inválido! Informe uma CEP de 8 números!");
    }
}

function finalizarConta(cliente) {
    document.querySelector("#CGC").value = "";
    document.querySelector("#CEP").value = "";
    document.querySelector("#ENDERECO").value = "";
    document.querySelector("#NUMERO").value = "";
    document.querySelector("#BAIRRO").value = "";
    document.querySelector("#FONE").value = "";
    document.querySelector("#CELULAR").value = "";
    document.querySelector("#NCIDADE1").value = "";
    document.querySelector("#NUF1").value = "";
    document.querySelector("#COMPLEMENTO").value = "";
    alert("Cadastro Concluído com Sucesso!");
}

function verificarLogin() {
    document.querySelector(".logar").pagina = document.querySelectorAll(".setVisible");

    setVisiblePrincipais();
    setInvisible();
    document.querySelector("section").style.display = "block";
    document.querySelector(".divLogin").style.display = "block";
    setVisible("section");
    setVisible(".divLogin");

    document.querySelector("#imgHeaderSec").addEventListener("click", irInicio);
    document.querySelector("#h1HeaderSec").addEventListener("click", irInicio);
    document.querySelector("#h2HeaderSec").addEventListener("click", irInicio);

    document.querySelector("#criarContaLogin").addEventListener("click", criarConta);
    document.querySelector("#entrarLogin").addEventListener("click", fazerLogin);
}

function fazerLogin() {
    if (document.querySelector("#EMAILL").value.trim() !== "" && document.querySelector("#SENHAL").value.trim() !== "")
    {
        requisicaoHTTP("projetoPratico", "cliente", "fazerLogin", logar, alert, "&EMAIL=" + document.querySelector("#EMAILL").value
                + "&SENHA=" + document.querySelector("#SENHAL").value);
    } else {
        alert("Preencha todos os campos obrigatórios!");
    }
}

function logar(cliente) {
    var logon = {'cliente': []};
    logon.cliente.push({'codigo': cliente, 'data': new Date()});
    window.localStorage.setItem('logon', JSON.stringify(logon));
    validarLogon();
    document.querySelector("#EMAILL").value = "";
    document.querySelector("#SENHAL").value = "";

    var voltar = document.querySelector(".logar").pagina;

    setInvisible();

    document.querySelector("header").style.display = "block";
    document.querySelector("nav").style.display = "block";
    document.querySelector("article").style.display = "block";
    document.querySelector("footer").style.display = "block";

    for (var i = 0; i < voltar.length; i++)
    {
        setVisible("." + voltar[i].className);
    }

    document.querySelector(".logar").pagina = undefined;

}

function deslogar() {
    localStorage.removeItem('logon');
    validarLogon();
}

function setVisiblePrincipais() {
    setVisible("header");
    setVisible("nav");
    setVisible("article");
    setVisible("footer");
}

function irMinhaConta() {
    setInvisible();
    document.querySelector(".divMenuCliente").style.display = "block";
    document.querySelector(".contemBemVindoCli").style.display = "block";
    setVisible(".divMenuCliente");
    setVisible(".contemBemVindoCli");

    document.querySelector(".editMeusDados").addEventListener("click", verMeusDados);
}

function verMeusDados() {
    setInvisible();
    document.querySelector(".divMenuCliente").style.display = "block";
    setVisible(".divMenuCliente");
    document.querySelector(".contemMeusDados").style.display = "block";
    setVisible(".contemMeusDados");

    var logon = {};
    logon = window.localStorage.getItem("logon");
    logon = JSON.parse(logon);

    if (logon["cliente"][0]["codigo"])
    {
        requisicaoHTTP("projetoPratico", "cliente", "buscarDadosCli", setarClienteCampos, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]);
    }
}

function setarClienteCampos(cliente) {
    document.querySelector("#NOMEE").value = cliente["linhas"][0]["NOMEFILIAL"];
    document.querySelector("#EMAILE").value = cliente["linhas"][0]["EMAIL"];
    document.querySelector("#USUARIOPXE").value = cliente["linhas"][0]["USUARIOPX"];
    document.querySelector("#FONEE").value = cliente["linhas"][0]["FONE"];
    document.querySelector("#CELULARE").value = cliente["linhas"][0]["CELULAR"];
    document.querySelector("#COMPLEMENTOE").value = cliente["linhas"][0]["COMPLEMENTO"];
    document.querySelector("#BAIRROE").value = cliente["linhas"][0]["BAIRRO"];
    document.querySelector("#NUMEROE").value = cliente["linhas"][0]["NUMERO"];
    document.querySelector("#ENDERECOE").value = cliente["linhas"][0]["ENDERECO"];
    document.querySelector("#CEPE").value = cliente["linhas"][0]["CEP"];
    document.querySelector("#NCIDADE1E").value = cliente["linhas"][0]["CIDADE"];
    document.querySelector("#NUF1E").value = cliente["linhas"][0]["UF"];
    document.querySelector("#CEPE").value = cliente["linhas"][0]["CEP"];
    document.querySelector("#CGCE").value = cliente["linhas"][0]["CGC"];

}

init();