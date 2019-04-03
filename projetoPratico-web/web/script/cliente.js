function init() {
    document.querySelector(".cadastrar").addEventListener("click", criarConta);
    document.querySelector(".logar").addEventListener("click", verificarLogin);
}

function irInicio() {
    document.querySelector("#NOME").value = "";
    document.querySelector("#EMAIL").value = "";
    document.querySelector("#SENHAPX").value = "";
    document.querySelector("#confirmSenha").value = "";

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
    setVisible("header");
    setVisible("nav");
    setVisible("article");
    setVisible("footer");
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
                    alert("CEP inválido! O CEP informado não existe!");
                } else {
                    document.querySelector("#NCIDADE1").value = dados.localidade;
                    document.querySelector("#NUF1").value = dados.uf;
                }
            }
        });
        http.send(null);
    } else {
        alert("CEP inválido! Informe uma CEP de 8 números!");
    }
}

function finalizarConta(cliente) {
    alert("Cadastro Concluído com Sucesso!");
}

function verificarLogin() {
    setVisible("header");
    setVisible("nav");
    setVisible("article");
    setVisible("footer");
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
    carrinho = {'logon': []};

    alert("ok");
}

init();