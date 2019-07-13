function init() {
    document.querySelector("body").addEventListener("load", verificarTempo);
    document.querySelector(".cadastrar").addEventListener("click", criarConta); //teste);
    document.querySelector(".logar").addEventListener("click", verificarLogin);
    document.querySelector(".desLogar").addEventListener("click", deslogar);
    document.querySelector(".minhaConta").addEventListener("click", irMinhaConta);
    document.querySelector("#enviarMensagem").addEventListener("click", enviarMensagem);

    validarLogon();
    verificarTempo();
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
        requisicaoHTTP("projetoPratico", "cliente", "inserirClienteEnd", enviarEmailConfirmacao, alert, "&CCLIFOR=" + botao["CCLIFOR"]
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
    var cep = document.querySelector("#CEP").value.trim();

    if (cep.length === 8) {
        http.open('GET', 'https://viacep.com.br/ws/' + cep + '/json/', true);
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
    } else if (cep.length > 0) {
        document.querySelector("#NCIDADE1").value = "";
        document.querySelector("#NUF1").value = "";
        alert("CEP inválido! Informe uma CEP de 8 números!");
    } else {
        document.querySelector("#NCIDADE1").value = "";
        document.querySelector("#NUF1").value = "";
    }
}

function enviarEmailConfirmacao() {
    var botao = document.querySelector("#salvarDadosConta").dados;
    requisicaoHTTP("projetoPratico", "cliente", "enviarEmailConfirmacao", finalizarConta, alert, "&DESTINATARIO=" + botao["EMAIL"]
            + "&NOME=" + botao["NOME"]);
}

function finalizarConta(mensagem) {
    alert(mensagem);

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

    var logon = {'cliente': []};
    logon.cliente.push({'codigo': document.querySelector("#salvarDadosConta").dados["CCLIFOR"], 'data': new Date()});
    window.localStorage.setItem('logon', JSON.stringify(logon));

    validarLogon();

    setInvisible();
    document.querySelector("header").style.display = "block";
    document.querySelector("nav").style.display = "block";
    document.querySelector("article").style.display = "block";
    document.querySelector("footer").style.display = "block";
    irMinhaConta();

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
    document.querySelector("#esqueciSenha").addEventListener("click", recuperarSenha);
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

function recuperarSenha() {
    if (document.querySelector("#EMAILL").value.trim() !== "")
    {
        var confirmou = confirm("Sua senha será redefinida e enviada para o e-mail informado. \nDeseja continuar?");
        if (confirmou)
        {
            requisicaoHTTP("projetoPratico", "cliente", "redefinirSenha", alert, alert, "&EMAIL=" + document.querySelector("#EMAILL").value);
        }
    } else {
        alert("Para redefinir a sua senha você precisa informar o seu e-mail.");
    }
}

function irMinhaConta() {
    var logon = {};
    logon = window.localStorage.getItem("logon");
    logon = JSON.parse(logon);

    requisicaoHTTP("projetoPratico", "cliente", "buscarEnderecoCli", function (enderecos) {
        setInvisible();
        document.querySelector(".divMenuCliente").style.display = "block";
        document.querySelector(".contemBemVindoCli").style.display = "block";
        setVisible(".divMenuCliente");
        setVisible(".contemBemVindoCli");

        var ul = document.querySelector(".liAdicionarEnd ul"), li;
        ul.innerHTML = "";

        for (var i = 0; i < enderecos["linhas"].length; i++)
        {
            li = document.createElement('li');
            li.setAttribute('class', 'estiloAdicionarEnd');
            li.setAttribute('id', enderecos["linhas"][i]["SCLIENDENT"]);
            li.innerText = enderecos["linhas"][i]["CLIENDENT"];
            ul.appendChild(li);
            li.addEventListener("click", listarEndereco);
        }

        li = document.createElement('li');
        li.setAttribute('class', 'estiloAdicionarEnd');
        li.innerText = "Endereço ";
        ul.appendChild(li);

        i = document.createElement('i');
        li.appendChild(i);
        i.setAttribute('class', 'fas fa-plus');

        li.addEventListener("click", cadastrarEndereco);

        document.querySelector(".verMeusDados").addEventListener("click", verMeusDados);
        document.querySelector(".verMinhasCompras").addEventListener("click", chamarMinhasCompras);
    }, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]);
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

    document.querySelector("#editarMeusDados").addEventListener("click", editarMeusDados);
    document.querySelector("#cancelarMeusDados").addEventListener("click", cancelarMeusDados);
    document.querySelector("#salvarMeusDados").addEventListener("click", salvarMeusDados);

    document.querySelector("#SENHAPXE").addEventListener("blur", validarSenhaAntiga);

    document.querySelector("#CEPE").addEventListener("blur", verificarCepe);

    if (logon["cliente"][0]["codigo"])
    {
        requisicaoHTTP("projetoPratico", "cliente", "buscarDadosCli", setarClienteCampos, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]);
    }
}

function setarClienteCampos(cliente) {
    if (cliente)
    {
        document.querySelector("#cancelarMeusDados").cliente = cliente;
    }

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
    document.querySelector("#SENHAPXE").value = '';
    document.querySelector("#SENHANOVAE").value = '';
    document.querySelector("#SENHADENOVOE").value = '';
}

function listarEndereco(e) {
    if (document.querySelector("#contemEnd" + e.target.id))
    {
        e.target.parentNode.parentNode.parentNode.parentNode.parentNode.removeChild(document.querySelector("#contemEnd" + e.target.id));
    }

    requisicaoHTTP("projetoPratico", "cliente", "buscarEndereco", function (enderecos) {
        setInvisible();
        document.querySelector(".divMenuCliente").style.display = "block";
        setVisible(".divMenuCliente");

        var i, form, div, divTitulo, divDados, label, input, h1;

        div = document.createElement("div");
        div.style.display = 'none';
        div.setAttribute('id', "contemEnd" + enderecos["linhas"][0]["SCLIENDENT"]);
        div.setAttribute('class', "contemEnd");
        document.querySelector("article").appendChild(div);

        divTitulo = document.createElement("div");
        divTitulo.setAttribute('class', "divDadosConta");
        div.appendChild(divTitulo);

        h1 = document.createElement("h1");
        h1.innerText = "Meus Dados / " + enderecos["linhas"][0]["CLIENDENT"];
        divTitulo.appendChild(h1);

        divDados = document.createElement("div");
        divDados.setAttribute('class', "meusDadosEnd");
        div.appendChild(divDados);

        form = document.createElement("form");
        divDados.appendChild(form);

        div = document.createElement("div");
        div.setAttribute('class', "divCamposDadosConta divCamposDadosConta5");
        form.appendChild(div)

        label = document.createElement("label");
        label.innerText = "CEP*";
        div.appendChild(label);

        input = document.createElement("input");
        input.setAttribute('class', "camposMeusDados camposMeusDados5");
        input.setAttribute('id', "CEPE" + (parseFloat(i) + parseFloat(1)));
        input.setAttribute('type', "number");
        input.setAttribute('readonly', "true");
        input.setAttribute('value', enderecos["linhas"][0]["CEP"]);
        div.appendChild(input);

        div = document.createElement("div");
        div.setAttribute('class', "divCamposDadosConta divCamposDadosConta6");
        form.appendChild(div)

        label = document.createElement("label");
        label.innerText = "Cidade*";
        div.appendChild(label);

        input = document.createElement("input");
        input.setAttribute('class', "camposMeusDados camposMeusDados6");
        input.setAttribute('id', "NCIDADE1E" + (parseFloat(i) + parseFloat(1)));
        input.setAttribute('type', "text");
        input.setAttribute('readonly', "true");
        input.setAttribute('value', enderecos["linhas"][0]["CIDADE"]);
        div.appendChild(input);

        div = document.createElement("div");
        div.setAttribute('class', "divCamposDadosConta divCamposDadosConta2");
        form.appendChild(div);

        label = document.createElement("label");
        label.innerText = "UF*";
        div.appendChild(label);

        input = document.createElement("input");
        input.setAttribute('class', "camposMeusDados camposMeusDados2");
        input.setAttribute('id', "NUF1E" + (parseFloat(i) + parseFloat(1)));
        input.setAttribute('type', "text");
        input.setAttribute('readonly', "true");
        input.setAttribute('value', enderecos["linhas"][0]["UF"]);
        div.appendChild(input);

        div = document.createElement("div");
        div.setAttribute('class', "divCamposDadosConta divCamposDadosConta1");
        form.appendChild(div);

        label = document.createElement("label");
        label.innerText = "Endereço*";
        div.appendChild(label);

        input = document.createElement("input");
        input.setAttribute('class', "camposMeusDados camposMeusDados1");
        input.setAttribute('id', "ENDERECOE" + (parseFloat(i) + parseFloat(1)));
        input.setAttribute('type', "text");
        input.setAttribute('readonly', "true");
        input.setAttribute('value', enderecos["linhas"][0]["ENDERECO"]);
        div.appendChild(input);

        div = document.createElement("div");
        div.setAttribute('class', "divCamposDadosConta divCamposDadosConta1");
        form.appendChild(div);

        label = document.createElement("label");
        label.innerText = "Bairro*";
        div.appendChild(label);

        input = document.createElement("input");
        input.setAttribute('class', "camposMeusDados camposMeusDados1");
        input.setAttribute('id', "BAIRROE" + (parseFloat(i) + parseFloat(1)));
        input.setAttribute('type', "text");
        input.setAttribute('readonly', "true");
        input.setAttribute('value', enderecos["linhas"][0]["BAIRRO"]);
        div.appendChild(input);

        div = document.createElement("div");
        div.setAttribute('class', "divCamposDadosConta divCamposDadosConta3");
        form.appendChild(div);

        label = document.createElement("label");
        label.innerText = "Número*";
        div.appendChild(label);

        input = document.createElement("input");
        input.setAttribute('class', "camposMeusDados camposMeusDados3");
        input.setAttribute('id', "NUMEROE" + (parseFloat(i) + parseFloat(1)));
        input.setAttribute('type', "text");
        input.setAttribute('readonly', "number");
        input.setAttribute('value', enderecos["linhas"][0]["NUMERO"]);
        div.appendChild(input);

        div = document.createElement("div");
        div.setAttribute('class', "divCamposDadosConta divCamposDadosConta4");
        form.appendChild(div);

        label = document.createElement("label");
        label.innerText = "Complemento";
        div.appendChild(label);

        input = document.createElement("input");
        input.setAttribute('class', "camposMeusDados camposMeusDados4");
        input.setAttribute('id', "COMPLEMENTOE" + (parseFloat(i) + parseFloat(1)));
        input.setAttribute('type', "text");
        input.setAttribute('readonly', "true");
        input.setAttribute('value', enderecos["linhas"][0]["COMPLEMENTO"]);
        div.appendChild(input);

        document.querySelector("#contemEnd" + e.target.id).style.display = "block";
        setVisible("#contemEnd" + e.target.id);
    }, alert, "&SCLIENDENT=" + e.target.id);

}

function cadastrarEndereco(e) {
    if (document.querySelector(".adicionarEnd"))
    {
        e.target.parentNode.parentNode.parentNode.parentNode.parentNode.removeChild(document.querySelector(".adicionarEnd"));
    }

    setInvisible();
    document.querySelector(".divMenuCliente").style.display = "block";
    setVisible(".divMenuCliente");

    var form, div, divTitulo, divDados, label, input, h1;

    div = document.createElement("div");
    div.setAttribute('class', "adicionarEnd setVisible");
    document.querySelector("article").appendChild(div);

    divTitulo = document.createElement("div");
    divTitulo.setAttribute('class', "divDadosConta");
    div.appendChild(divTitulo);

    h1 = document.createElement("h1");
    h1.innerText = "Meus Dados / Novo Endereço ";
    divTitulo.appendChild(h1);

    divDados = document.createElement("div");
    divDados.setAttribute('class', "meusDadosEnd");
    div.appendChild(divDados);

    form = document.createElement("form");
    divDados.appendChild(form);

    div = document.createElement("div");
    div.setAttribute('class', "divCamposDadosConta divCamposDadosConta5");
    form.appendChild(div);

    label = document.createElement("label");
    label.innerText = "CEP*";
    div.appendChild(label);

    input = document.createElement("input");
    input.setAttribute('class', "camposMeusDados camposMeusDados5");
    input.setAttribute('id', "CEPEN");
    input.setAttribute('type', "number");
    div.appendChild(input);

    div = document.createElement("div");
    div.setAttribute('class', "divCamposDadosConta divCamposDadosConta6");
    form.appendChild(div)

    label = document.createElement("label");
    label.innerText = "Cidade*";
    div.appendChild(label);

    input = document.createElement("input");
    input.setAttribute('class', "camposMeusDados camposMeusDados6");
    input.setAttribute('id', "NCIDADE1EN");
    input.setAttribute('type', "text");
    div.appendChild(input);

    div = document.createElement("div");
    div.setAttribute('class', "divCamposDadosConta divCamposDadosConta2");
    form.appendChild(div);

    label = document.createElement("label");
    label.innerText = "UF*";
    div.appendChild(label);

    input = document.createElement("input");
    input.setAttribute('class', "camposMeusDados camposMeusDados2");
    input.setAttribute('id', "NUF1EN");
    input.setAttribute('type', "text");
    div.appendChild(input);

    div = document.createElement("div");
    div.setAttribute('class', "divCamposDadosConta divCamposDadosConta1");
    form.appendChild(div);

    label = document.createElement("label");
    label.innerText = "Endereço*";
    div.appendChild(label);

    input = document.createElement("input");
    input.setAttribute('class', "camposMeusDados camposMeusDados1");
    input.setAttribute('id', "ENDERECOEN");
    input.setAttribute('type', "text");
    div.appendChild(input);

    div = document.createElement("div");
    div.setAttribute('class', "divCamposDadosConta divCamposDadosConta1");
    form.appendChild(div);

    label = document.createElement("label");
    label.innerText = "Bairro*";
    div.appendChild(label);

    input = document.createElement("input");
    input.setAttribute('class', "camposMeusDados camposMeusDados1");
    input.setAttribute('id', "BAIRROEN");
    input.setAttribute('type', "text");
    div.appendChild(input);

    div = document.createElement("div");
    div.setAttribute('class', "divCamposDadosConta divCamposDadosConta3");
    form.appendChild(div);

    label = document.createElement("label");
    label.innerText = "Número*";
    div.appendChild(label);

    input = document.createElement("input");
    input.setAttribute('class', "camposMeusDados camposMeusDados3");
    input.setAttribute('id', "NUMEROEN");
    input.setAttribute('type', "text");
    div.appendChild(input);

    div = document.createElement("div");
    div.setAttribute('class', "divCamposDadosConta divCamposDadosConta4");
    form.appendChild(div);

    label = document.createElement("label");
    label.innerText = "Complemento";
    div.appendChild(label);

    input = document.createElement("input");
    input.setAttribute('class', "camposMeusDados camposMeusDados4");
    input.setAttribute('id', "COMPLEMENTOEN");
    input.setAttribute('type', "text");
    div.appendChild(input);

    div = document.createElement("div");
    div.setAttribute('class', "btnMeusDadosEnd");
    div.setAttribute('id', "salvarEnd");
    div.innerText = "Salvar";
    document.querySelector(".adicionarEnd").appendChild(div);
    div.addEventListener("click", salvarEnd);

    var campos = document.querySelector(".adicionarEnd").querySelectorAll(".camposMeusDados");
    for (var i = 0; i < campos.length; i++)
    {
        campos[i].style.backgroundColor = "rgba(239, 239, 239, 0.05)";
    }
}

function salvarEnd() {
    if (document.querySelector("#CEPEN").value.trim() !== "" && document.querySelector("#ENDERECOEN").value.trim() !== ""
            && document.querySelector("#NUMEROEN").value.trim() !== "" && document.querySelector("#BAIRROEN").value.trim())
    {
        var logon = {};
        logon = window.localStorage.getItem("logon");
        logon = JSON.parse(logon);

        requisicaoHTTP("projetoPratico", "cliente", "salvarEndereco", function (msg) {
            alert(msg);
            verMeusDados();
        }, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]
                + "&CEP=" + document.querySelector("#CEPEN").value + "&ENDERECO=" + document.querySelector("#ENDERECOEN").value
                + "&NUMERO=" + document.querySelector("#NUMEROEN").value + "&BAIRRO=" + document.querySelector("#BAIRROEN").value
                + "&NCIDADE1=" + document.querySelector("#NCIDADE1EN").value + "&NUF1=" + document.querySelector("#NUF1EN").value
                + "&COMPLEMENTO=" + document.querySelector("#COMPLEMENTOEN").value);
    } else {
        alert("Preencha todos os campos obrigatórios!");
    }
}

function editarMeusDados() {
    document.querySelector("#editarMeusDados").editando = true;
    var campos = document.querySelectorAll(".camposMeusDados");
    for (var i = 0; i < campos.length; i++)
    {
        campos[i].removeAttribute("readonly");
        campos[i].style.backgroundColor = "rgba(239, 239, 239, 0.05)";
    }
}

function cancelarMeusDados() {
    setarCamposCinza();
    setarClienteCampos(document.querySelector("#cancelarMeusDados").cliente);
    document.querySelector("#editarMeusDados").editando = false;
}

function validarSenhaAntiga() {
    var senhaAntiga = document.querySelector("#SENHAPXE").value.trim();

    var logon = {};
    logon = window.localStorage.getItem("logon");
    logon = JSON.parse(logon);

    if (senhaAntiga.length > 0) {
        requisicaoHTTP("projetoPratico", "cliente", "validarSenhaAntiga", alert, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]
                + "&SENHAPX=" + senhaAntiga);
    }
}

function salvarMeusDados() {
    if (document.querySelector("#editarMeusDados").editando)
    {
        var senhaAntiga = document.querySelector("#SENHAPXE").value.trim();
        var senhaNova = document.querySelector("#SENHANOVAE").value.trim();
        var senhaConfirm = document.querySelector("#SENHADENOVOE").value.trim();

        if (senhaAntiga.length > 0 || senhaNova.length > 0 || senhaConfirm.length > 0)
        {
            if (!(senhaAntiga.length > 0 && senhaNova.length > 0 && senhaConfirm.length > 0))
            {
                alert("Informe todos os campos referentes a senha!");
                return;
            } else {
                if (senhaNova !== senhaConfirm) {
                    alert("As senhas novas não coicidem!");
                    return;
                }
            }
        }

        var logon = {};
        logon = window.localStorage.getItem("logon");
        logon = JSON.parse(logon);

        requisicaoHTTP("projetoPratico", "cliente", "salvarEditCli", setarCamposCinza, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]
                + "&NOME=" + document.querySelector("#NOMEE").value + "&EMAIL=" + document.querySelector("#EMAILE").value
                + "&USUARIOPX=" + document.querySelector("#USUARIOPXE").value + "&SENHANOVA=" + senhaNova + "&CPF=" + document.querySelector("#CGCE").value
                + "&CEP=" + document.querySelector("#CEPE").value + "&ENDERECO=" + document.querySelector("#ENDERECOE").value
                + "&NUMERO=" + document.querySelector("#NUMEROE").value + "&BAIRRO=" + document.querySelector("#BAIRROE").value
                + "&FONE=" + document.querySelector("#FONEE").value + "&CELULAR=" + document.querySelector("#CELULARE").value
                + "&NCIDADE1=" + document.querySelector("#NCIDADE1E").value + "&NUF1=" + document.querySelector("#NUF1E").value
                + "&COMPLEMENTO=" + document.querySelector("#COMPLEMENTOE").value + "&SENHAPX=" + senhaAntiga);

    }
}

function setarCamposCinza(msg) {
    var campos = document.querySelectorAll(".camposMeusDados");
    for (var i = 0; i < campos.length; i++)
    {
        campos[i].setAttribute("readonly", true);
        campos[i].style.backgroundColor = "rgba(203, 209, 215, .3)";
    }

    if (msg) {
        alert(msg);
    }
}

function verificarCepe() {
    var http = new XMLHttpRequest();
    var cep = document.querySelector("#CEPE").value.trim();

    if (cep.length === 8) {
        http.open('GET', 'https://viacep.com.br/ws/' + cep + '/json/', true);
        http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        http.addEventListener('load', function () {
            if (http.status === 200) {
                var dados = JSON.parse(http.response);
                if (dados.erro) {
                    document.querySelector("#NCIDADE1E").value = "";
                    document.querySelector("#NUF1E").value = "";
                    alert("CEP inválido! O CEP informado não existe!");
                } else {
                    document.querySelector("#NCIDADE1E").value = dados.localidade;
                    document.querySelector("#NUF1E").value = dados.uf;
                }
            }
        });
        http.send(null);
    } else if (cep.length > 0) {
        document.querySelector("#NCIDADE1E").value = "";
        document.querySelector("#NUF1E").value = "";
        alert("CEP inválido! Informe uma CEP de 8 números!");
    } else {
        document.querySelector("#NCIDADE1E").value = "";
        document.querySelector("#NUF1E").value = "";
    }
}

function chamarMinhasCompras() {
    var logon = {};
    logon = window.localStorage.getItem("logon");
    logon = JSON.parse(logon);

    requisicaoHTTP("projetoPratico", "cliente", "listarMinhasCompras", verMinhasCompras, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]);
}

function verMinhasCompras(compras) {
    setInvisible();
    document.querySelector(".divMenuCliente").style.display = "block";
    setVisible(".divMenuCliente");
    document.querySelector(".contemMinhasCompras").style.display = "block";
    setVisible(".contemMinhasCompras");

    var divComprasTable = document.querySelector(".comprasTable");
    divComprasTable.innerHTML = "";
    var tr, td, th, status, spn, iaws, dv, hUm, tbody, thead, table, tableItem;

    if (compras !== null && compras["linhas"].length !== 0) {
        table = document.createElement('table');
        table.setAttribute('class', 'tableCompras');
        divComprasTable.appendChild(table);

        thead = document.createElement('thead');
        table.appendChild(thead);

        tr = document.createElement('tr');
        thead.appendChild(tr);

        th = document.createElement('th');
        th.innerText = "Nº Pedido";
        th.setAttribute('class', 'comprasTableTh');
        tr.appendChild(th);

        th = document.createElement('th');
        th.innerText = "Data Pedido";
        th.setAttribute('class', 'comprasTableTh');
        tr.appendChild(th);

        th = document.createElement('th');
        th.innerText = "Quantidade";
        th.setAttribute('class', 'comprasTableTh');
        tr.appendChild(th);

        th = document.createElement('th');
        th.innerText = "Valor Total";
        th.setAttribute('class', 'comprasTableTh');
        tr.appendChild(th);

        th = document.createElement('th');
        th.innerText = "Data Entrega";
        th.setAttribute('class', 'comprasTableTh');
        tr.appendChild(th);

        th = document.createElement('th');
        th.innerText = "Status";
        th.setAttribute('class', 'comprasTableTh');
        tr.appendChild(th);

        th = document.createElement('th');
        th.innerText = "Itens";
        th.setAttribute('class', 'comprasTableTh');
        tr.appendChild(th);

        tbody = document.createElement('tbody');
        table.appendChild(tbody);

        for (var i = 0; i < compras["linhas"].length; i++)
        {
            tr = document.createElement('tr');
            tr.setAttribute('class', 'itemCompraCapa');
            tbody.appendChild(tr);

            td = document.createElement('td');
            td.innerText = compras["linhas"][i]['PEDIDO'];
            td.setAttribute('class', 'pedido');
            tr.appendChild(td);

            td = document.createElement('td');
            td.innerText = compras["linhas"][i]['DATA'];
            tr.appendChild(td);

            td = document.createElement('td');
            td.innerText = parseFloat(compras["linhas"][i]['QTDE']).toFixed(3).replace(".", ",");
            tr.appendChild(td);

            td = document.createElement('td');
            td.innerText = "R$ " + parseFloat(compras["linhas"][i]['TOTAL']).toFixed(2).replace(".", ",");
            tr.appendChild(td);

            td = document.createElement('td');
            td.innerText = compras["linhas"][i]['PREVDT'];
            tr.appendChild(td);

            td = document.createElement('td');
            td.innerText = compras["linhas"][i]['STATUS'];
            tr.appendChild(td);

            td = document.createElement('td');
            tr.appendChild(td);
            spn = document.createElement('span');
            td.appendChild(spn);
            spn.setAttribute('class', 'verItensCompra');

            iaws = document.createElement('i');
            spn.appendChild(iaws);
            iaws.setAttribute('class', 'fas fa-plus');

            tr = document.createElement('tr');
            tr.setAttribute('class', 'itemCompraDetalhe');
            tbody.appendChild(tr);

            td = document.createElement('td');
            td.setAttribute('colspan', '7');
            tr.appendChild(td);

            tableItem = document.createElement('table');
            tableItem.setAttribute('class', 'tableItem ' + compras["linhas"][i]['PEDIDO']);
            td.appendChild(tableItem);
        }


        var arr = document.querySelectorAll(".verItensCompra");
        for (var j = 0; j < arr.length; j++)
        {
            arr[j].addEventListener("click", chamarItensCompra);
        }

    } else {
        dv = document.createElement('div');
        dv.setAttribute('class', 'comprasVazio');
        divComprasTable.appendChild(dv);

        hUm = document.createElement('h1');
        hUm.innerText = 'Você ainda não pescou nada!';
        hUm.setAttribute('class', 'comprasVazioText');
        dv.appendChild(hUm);
    }
}

function chamarItensCompra(e) {
    requisicaoHTTP("projetoPratico", "cliente", "listarItensCompra", verItensCompra, alert,
            "&PEDIDO=" + e.target.parentNode.parentNode.parentNode.querySelector(".pedido").innerText);

    e.target.setAttribute('class', 'fas fa-minus');
    e.target.parentNode.removeEventListener("click", chamarItensCompra);
    e.target.parentNode.addEventListener("click", esconderItensCompra);
}

function verItensCompra(itens) {
    var table = document.getElementsByClassName("tableItem " + itens[0]['PEDIDO'])[0];
    var thead, tbody, tr, th, td, dvImg, img;

    thead = document.createElement('thead');
    table.appendChild(thead);

    tr = document.createElement('tr');
    thead.appendChild(tr);

    th = document.createElement('th');
    tr.appendChild(th);
    th.innerText = 'Produto';
    th.setAttribute('class', 'comprasItensTh');
    th.setAttribute('colspan', '2');

    th = document.createElement('th');
    th.setAttribute('class', 'comprasItensTh');
    tr.appendChild(th);
    th.innerText = 'Preço';

    th = document.createElement('th');
    th.setAttribute('class', 'comprasItensTh');
    tr.appendChild(th);
    th.innerText = 'Quantidade';

    th = document.createElement('th');
    th.setAttribute('class', 'comprasItensThTotal');
    tr.appendChild(th);
    th.innerText = 'Total';
    th.setAttribute('colspan', '2');

    tbody = document.createElement('tbody');
    table.appendChild(tbody);

    for (var i = 0; i < itens.length; i++)
    {
        tr = document.createElement('tr');
        tr.setAttribute('class', 'comprasItensTr');
        tbody.appendChild(tr);

        td = document.createElement('td');
        td.setAttribute('class', 'tdImg');
        tr.appendChild(td);

        dvImg = document.createElement('div');
        dvImg.setAttribute('class', 'divProdutoCar');
        td.appendChild(dvImg);

        img = document.createElement('img');
        img.setAttribute('class', 'fotoCarrinho');
        img.src = 'data:image/jpg;base64,' + itens[i]['IMAGEM'];
        dvImg.appendChild(img);

        td = document.createElement('td');
        tr.appendChild(td);
        td.innerText += itens[i]['CATEGORIA'];
        td.appendChild(document.createElement('br'));
        td.innerText += itens[i]['DESCRICAO'];

        td = document.createElement('td');
        tr.appendChild(td);
        td.innerText = 'R$ ' + itens[i]['PRECO'].toFixed(2).replace('.', ',');

        td = document.createElement('td');
        tr.appendChild(td);
        td.setAttribute('class', 'tdQtdeCompra');
        td.innerText = itens[i]['QTDE'].toFixed(3).replace('.', ',') + " " + itens[i]['UNIDADE'];

        td = document.createElement('td');
        td.setAttribute('class', 'tdTotal');
        tr.appendChild(td);
        td.innerText = "R$ " + itens[i]['TOTALITEM'].toFixed(2).replace('.', ',');
    }
    tr = document.createElement('tr');
    tbody.appendChild(tr);

    td = document.createElement('td');
    td.setAttribute('colspan', '3');
    tr.appendChild(td);

    td = document.createElement('td');
    td.innerText = 'Frete';
    td.setAttribute('class', 'tdFrete');
    tr.appendChild(td);

    td = document.createElement('td');
    td.setAttribute('class', 'tdTotal');
    td.innerText = "R$ " + itens[0]['FRETE'].toFixed(2).replace('.', ',');

    tr.appendChild(td);
}

function esconderItensCompra(e) {
    document.getElementsByClassName("tableItem " + e.target.parentNode.parentNode.parentNode.querySelector(".pedido").innerText)[0].textContent = "";
    e.target.setAttribute('class', 'fas fa-plus');
    e.target.parentNode.removeEventListener("click", esconderItensCompra);
    e.target.parentNode.addEventListener("click", chamarItensCompra);
}

function enviarMensagem(e) {
    if (document.querySelector("#emailClienteMensagem").value.trim() !== "" && document.querySelector("#nomeClienteMensagem").value.trim() !== "" &&
            document.querySelector("#mensagemSobre").value.trim() !== "")
    {
        requisicaoHTTP("projetoPratico", "cliente", "enviarMensagemCliente", limparCampos, alert,
                "&EMAIL=" + document.querySelector("#emailClienteMensagem").value
                + "&NOME= " + document.querySelector("#nomeClienteMensagem").value
                + "&MENSAGEM=" + document.querySelector("#mensagemSobre").value);
    } else {
        alert("Preencha todos os campos obrigatórios!");
    }
}

function limparCampos(e) {
    alert(e);

    document.querySelector("#emailClienteMensagem").value = "";
    document.querySelector("#nomeClienteMensagem").value = "";
    document.querySelector("#mensagemSobre").value = "*Mensagem";
}

init();