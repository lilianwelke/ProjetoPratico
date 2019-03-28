function init() {
    document.querySelector(".cadastrar").addEventListener("click", criarConta);
}

function criarConta() {
    document.querySelector("header").style.display = "none";
    document.querySelector("nav").style.display = "none";
    document.querySelector("article").style.display = "none";
    document.querySelector("footer").style.display = "none";
    document.querySelector("section").style.display = "block";
    document.querySelector(".divCriarConta").style.display = "block";

    document.querySelector("#imgHeaderSec").addEventListener("click", irInicio);
    document.querySelector("#h1HeaderSec").addEventListener("click", irInicio);
    document.querySelector("#h2HeaderSec").addEventListener("click", irInicio);

    document.querySelector("#criarConta").addEventListener("click", cadastrarCliente);

}

function irInicio() {
    document.querySelector("#NOME").value = "";
    document.querySelector("#EMAIL").value = "";
    document.querySelector("#SENHAPX").value = "";
    document.querySelector("#confirmSenha").value = "";
    document.querySelector("section").style.display = "none";
    document.querySelector(".divCriarConta").style.display = "none";
    document.querySelector(".divContinuarConta").style.display = "none";
    document.querySelector("header").style.display = "block";
    document.querySelector("nav").style.display = "block";
    document.querySelector("article").style.display = "block";
    document.querySelector("footer").style.display = "block";
    document.querySelector(".fundoInicio").style.display = "block";
}

function cadastrarCliente() {
    if (document.querySelector("#SENHAPX").value === document.querySelector("#confirmSenha").value)
    {
        requisicaoHTTP("projetoPratico", "cliente", "inserirCliente", continuarConta, alert, "&NOME=" + document.querySelector("#NOME").value
                + "&EMAIL=" + document.querySelector("#EMAIL").value + "&SENHAPX" + document.querySelector("#SENHAPX").value);
    } else {
        alert("As senhas informadas não são iguais!");
        document.querySelector("#SENHAPX").value = "";
        document.querySelector("#confirmSenha").value = "";
    }
}

function continuarConta(cliente) {
    document.querySelector("#NOME").value = "";
    document.querySelector("#EMAIL").value = "";
    document.querySelector("#SENHAPX").value = "";
    document.querySelector("#confirmSenha").value = "";
    document.querySelector(".divCriarConta").style.display = "none";
    document.querySelector(".divContinuarConta").style.display = "block";

    var nomeCliente = cliente["NOME"].split(" ");
    document.querySelector(".olaCliente").innerText += "Bem vinda(o) " + nomeCliente[0] + "! Precisamos de mais algumas informações";

    document.querySelector("#salvarDadosConta").addEventListener("click", continuarCadastro);
    document.querySelector("#salvarDadosConta").dados = cliente;

    document.querySelector("#CEP").addEventListener("blur", verificarCep);
}

function continuarCadastro() {
    var botao = document.querySelector("#salvarDadosConta").dados;
    requisicaoHTTP("projetoPratico", "cliente", "inserirClienteEnd", finalizarConta, alert, "&CCLIFOR=" + botao["CCLIFOR"]
            + "&NOME=" + botao["NOME"] + "&EMAIL=" + botao["EMAIL"] + "&CPF=" + document.querySelector("#CGC").value
            + "&CEP=" + document.querySelector("#CEP").value + "&ENDERECO=" + document.querySelector("#ENDERECO").value
            + "&NUMERO=" + document.querySelector("#NUMERO").value + "&BAIRRO=" + document.querySelector("#BAIRRO").value
            + "&FONE=" + document.querySelector("#FONE").value + "&CELULAR=" + document.querySelector("#CELULAR").value
            + "&NCIDADE1=" + document.querySelector("#NCIDADE1").value + "&NUF1=" + document.querySelector("#NUF1").value
            + "&COMPLEMENTO=" + document.querySelector("#COMPLEMENTO").value);
}

function verificarCep() {
    var http = new XMLHttpRequest();
    http.open('GET', 'https://viacep.com.br/ws/' + document.querySelector("#CEP").value + '/json/', true);
    http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    http.addEventListener('load', function () {
        if (http.status === 200) {
            var dados = JSON.parse(http.response);
            if (dados.erro) {
                funcaoErro(dados);
            } else {
                document.querySelector("#NCIDADE1").value = dados.localidade;
                document.querySelector("#NUF1").value = dados.uf;
                funcaoOK(dados);
            }
        } else if (http.status === 400) {

        }
    });
    http.send(null);

}

function finalizarConta(cliente) {
    alert("Cadastro Concluído com Sucesso!");
}

init();