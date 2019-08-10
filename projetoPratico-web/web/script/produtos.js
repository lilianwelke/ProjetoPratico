function init() {
    document.querySelector("#imgHeader").addEventListener("click", irInicio);
    document.querySelector("#h1Header").addEventListener("click", irInicio);
    document.querySelector("#h2Header").addEventListener("click", irInicio);
    document.querySelector("#conhecaPeixes").addEventListener("click", irPeixes);
    document.querySelector(".lupa").addEventListener("click", pesquisarProdutos);
    document.querySelector(".carrinho").addEventListener("click", irCarrinho);
    document.querySelector(".pesquisa").addEventListener("blur", limparPesquisa);
    document.querySelector("#ofertasSemana").addEventListener("click", irOfertas);
}

function irInicio() {
    setInvisible();
    document.querySelector(".fundoInicio").style.display = "block";
    setVisible(".fundoInicio");
}

function limparPesquisa(e) {
    setTimeout(function () {
        e.target.value = "";
    }
    , 10000);
}

function irOfertas() {
    setInvisible();
    document.querySelector(".listaOfertas").style.display = "block";
    setVisible(".listaOfertas");

    requisicaoHTTP("projetoPratico", "produtos", "listarOfertas", listarOfertas, alert, "&CODIGOREF='PX'&CSUBGRUPO=31");
}

function listarOfertas(produtos) {
    listarTodosProdutos(produtos, true);
}

function irPeixes(pesquisa) {
    setInvisible();
    document.querySelector(".divMenu").style.display = "block";
    document.querySelector(".listaProdutos").style.display = "block";
    setVisible(".divMenu");
    setVisible(".listaProdutos");

    requisicaoHTTP("projetoPratico", "produtos", "listarCategorias", listarCategorias, alert, "&CODIGOREF='PX'");
    if (pesquisa)
    {
        requisicaoHTTP("projetoPratico", "produtos", "listarProdutos", listarTodosProdutos, alert, "&CSUBGRUPO=31");
    }
}

function listarProdCategoria(e) {
    var categoria = e.target.id;
    if (categoria !== "31") {
        requisicaoHTTP("projetoPratico", "produtos", "listarProdCategoria", listarTodosProdutos, alert, "&CSUBGRUPO=31&CGRUPO=" + categoria);
    } else {
        requisicaoHTTP("projetoPratico", "produtos", "listarProdutos", listarTodosProdutos, alert, "&CSUBGRUPO=31");
    }
}

function pesquisarProdutos(e) {
    var conteudo = document.querySelector(".pesquisa").value;
    if (conteudo.trim() !== "") {
        irPeixes(false);
        requisicaoHTTP("projetoPratico", "produtos", "pesquisarProduto", listarTodosProdutos, alert, "&CSUBGRUPO=31&DESCRICAO=" + conteudo);
    }
}

function irDetalhes(e) {
    setInvisible();
    var cproduto = e.target.id;
    document.querySelector(".produtoMais").style.display = "block";
    setVisible(".produtoMais");

    if (cproduto.trim() !== "") {
        requisicaoHTTP("projetoPratico", "produtos", "retornarProduto", listarProduto, alert, "&CPRODUTO=" + cproduto);
    }
}

function irCarrinho(e) {
    setInvisible();
    document.querySelector(".divCarrinho").style.display = "block";
    setVisible(".divCarrinho");

    listarCarrinho();
}

function listarTodosProdutos(produtos, oferta) {
    var dv, hUm, p, dvVerMais, img, dvOferta;
    var div = (oferta ? document.querySelector('.listaOfertas') : document.querySelector('.listaProdutos'));
    div.innerHTML = "";

    if (oferta && produtos.length === 0)
    {
        hUm = document.createElement('h1');
        hUm.innerText = 'Que pena! \nNão há ofertas para hoje!';
        hUm.setAttribute('class', 'semOfertaText');
        div.appendChild(hUm);
    }

    for (var i = 0; i < produtos.length; i++)
    {
        dv = document.createElement('div');
        dv.setAttribute('class', 'produtoMini');
        if (produtos[i]['TEMSALDO'] === 0)
        {
            dv.style.filter = 'grayscale(50%)';
        }
        div.appendChild(dv);

        dvVerMais = document.createElement('div');
        dvVerMais.setAttribute('class', 'divFotoMini');
        dv.appendChild(dvVerMais);

        if (produtos[i]['DESCONTO'] > 0)
        {
            dvOferta = document.createElement('div');
            dvOferta.setAttribute('class', 'promocaoMini');
            dvOferta.innerText = '-' + produtos[i]['DESCONTO'] + '%';
            dvVerMais.appendChild(dvOferta);
        }

        img = document.createElement('img');
        img.src = 'data:image/jpg;base64,' + produtos[i]['IMAGEM'];
        img.setAttribute('class', 'fotoMini');
        dvVerMais.appendChild(img);

        hUm = document.createElement('h1');
        hUm.innerText = produtos[i]['PRODUTO'];
        dv.appendChild(hUm);

        p = document.createElement('p');
        p.innerText = 'R$ ' + produtos[i]['PRECO'].toFixed(2).replace('.', ',');
        dv.appendChild(p);

        dvVerMais = document.createElement('div');
        dvVerMais.setAttribute('class', 'verMaisMini');
        dvVerMais.setAttribute('id', produtos[i]['CPRODUTO']);
        dvVerMais.innerText = 'Conferir';
        dv.appendChild(dvVerMais);

    }
    var prods = document.querySelectorAll(".verMaisMini");
    for (var j = 0; j < prods.length; j++)
    {
        prods[j].addEventListener("click", irDetalhes);
    }
}

function listarCategorias(categorias) {
    var li;
    var ul = document.querySelector('.divMenu ul');
    ul.innerHTML = "";

    for (var i = 0; i < categorias.length; i++)
    {
        li = document.createElement('li');
        li.innerText = categorias[i]['GRUPO'];
        li.setAttribute('id', categorias[i]['CGRUPO']);
        ul.appendChild(li);
    }

    var lis = document.querySelectorAll(".divMenu li");
    for (var j = 0; j < lis.length; j++)
    {
        lis[j].addEventListener("click", listarProdCategoria);
    }
}

function listarProduto(produto) {
    var dv, hUm, p, pp, ppp, pppp, dvImg, dvComprarMais, img, input, saldo;
    var div = document.querySelector('.produtoMais');
    div.innerHTML = "";

    dvImg = document.createElement('div');
    dvImg.setAttribute('class', 'divFotoMais');
    div.appendChild(dvImg);

    img = document.createElement('img');
    img.src = 'data:image/jpg;base64,' + produto[0]['IMAGEM'];
    img.setAttribute('class', 'fotoMais');
    dvImg.appendChild(img);

    dv = document.createElement('div');
    dv.setAttribute('class', 'detalhesProduto');
    div.appendChild(dv);

    p = document.createElement('p');
    p.setAttribute('class', 'categoria');
    p.innerText = produto[0]['CATEGORIA'];
    dv.appendChild(p);

    hUm = document.createElement('h1');
    hUm.setAttribute('class', 'mercadoria');
    hUm.innerText = produto[0]['MERCADORIA'];
    dv.appendChild(hUm);

    pp = document.createElement('p');
    pp.setAttribute('class', 'produto');
    pp.innerText = produto[0]['PRODUTO'];
    dv.appendChild(pp);

    ppp = document.createElement('p');
    ppp.setAttribute('class', 'precoMais');
    ppp.innerText = 'R$ ' + produto[0]['PRECO'].toFixed(2).replace('.', ',');
    dv.appendChild(ppp);

    input = document.createElement('input');
    input.setAttribute('class', 'qtdeCompraMais');
    input.setAttribute('type', 'number');

    if (parseFloat(produto[0]['SALDO']) <= parseFloat(0)) {
        input.setAttribute('value', '0');
        input.setAttribute('readOnly', 'true');
    } else {
        input.setAttribute('value', '1');
        dv.appendChild(input);
    }

    dv.appendChild(input);

    pppp = document.createElement('p');
    pppp.setAttribute('class', 'unidadeMais');
    pppp.innerText = produto[0]['UN'];
    dv.appendChild(pppp);

    saldo = document.createElement('p');

    if (parseFloat(produto[0]['SALDO']) <= parseFloat(0)) {
        saldo.setAttribute('class', 'semSaldoMais');
        saldo.innerText = 'Produto Indisponível';
    } else {
        saldo.setAttribute('class', 'saldoMais');
        saldo.innerText = produto[0]['SALDO'] + ' ' + produto[0]['UN'] + ' disponíveis';
    }

    dv.appendChild(saldo);

    if (parseFloat(produto[0]['SALDO']) > parseFloat(0)) {
        dvComprarMais = document.createElement('div');
        dvComprarMais.setAttribute('class', 'comprarMais');
        dvComprarMais.setAttribute('id', produto[0]['CPRODUTO']);
        dvComprarMais.innerText = 'Adicionar ao Carrinho';
        dv.appendChild(dvComprarMais);

        document.querySelector(".comprarMais").addEventListener("click", adicionarCarrinho);
    }
}

function adicionarCarrinho(e) {
    var produto = e.target.parentNode.parentNode;
    var carrinho;
    var foto = produto.querySelector('.fotoMais').src;
    var categoria = produto.querySelector('.categoria').innerText;
    var mercadoria = produto.querySelector('.mercadoria').innerText;
    var descricao = produto.querySelector('.produto').innerText;
    var preco = produto.querySelector('.precoMais').innerText;
    var qtde = produto.querySelector('.qtdeCompraMais').value;
    var un = produto.querySelector('.unidadeMais').innerText;
    var cproduto = this.id;
    var verificaSeJaTem = false;
    var saldo = produto.querySelector('.saldoMais').innerText.split(" ", 1);

    if (parseFloat(qtde) <= parseFloat(saldo[0]))
    {
        if (localStorage.carrinho) {
            carrinho = JSON.parse(localStorage.carrinho);
            localStorage.setItem('carrinho', JSON.stringify(carrinho));

            for (var i = 0; i < carrinho.produtos.length; i++) {
                if (cproduto === carrinho['produtos'][i]['cproduto']) {
                    carrinho.produtos.find(function (obj, idx, arr) {
                        if (obj.cproduto === cproduto) {
                            arr[idx].qtde = parseFloat(arr[idx].qtde) + parseFloat(qtde);
                            verificaSeJaTem = true;
                            return true;
                        } else {
                            return false;
                        }
                    });
                }
            }
        } else {
            carrinho = {'produtos': []};
        }

        if (!verificaSeJaTem) {
            carrinho.produtos.push({'cproduto': cproduto, 'foto': foto, 'categoria': categoria, 'mercadoria': mercadoria, 'descricao': descricao,
                'preco': preco, 'qtde': qtde, 'un': un});
        }
        window.localStorage.setItem('carrinho', JSON.stringify(carrinho));
        irCarrinho();
    } else {
        alert('Quantidade requisitada é maior do que o saldo em estoque!');
    }
}

function listarCarrinho() {
    var logon = {};
    logon = window.localStorage.getItem("logon");
    logon = JSON.parse(logon);

    var itensCarrinho = {};
    itensCarrinho = window.localStorage.getItem('carrinho');
    itensCarrinho = JSON.parse(itensCarrinho);

    document.querySelector('.divCarrinho').innerHTML = '';

    var divCarrinho = document.querySelector('.divCarrinho');
    var subtotal = 0.0;
    var dv, table, thead, tbody, tr, th, td, dvImg, img, inpt, spn, iaws, cspn, hUmDiv, hUm, i, dvFinalizar, arr, input, dvCalcular;

    hUmDiv = document.createElement('div');
    hUmDiv.setAttribute('class', 'h1DivCarrinho');
    divCarrinho.appendChild(hUmDiv);

    hUm = document.createElement('h1');
    hUm.innerText = 'Carrinho';
    hUmDiv.appendChild(hUm);

    spn = document.createElement('span');
    spn.setAttribute('class', 'atualizaCarrinho');
    hUmDiv.appendChild(spn);

    i = document.createElement('i');
    i.setAttribute('class', 'fas fa-sync-alt');
    spn.appendChild(i);

    if (itensCarrinho !== null && itensCarrinho.produtos.length !== 0) {

        dv = document.createElement('div');
        dv.setAttribute('class', 'carrinhoTable');
        divCarrinho.appendChild(dv);

        table = document.createElement('table');
        dv.appendChild(table);

        thead = document.createElement('thead');
        table.appendChild(thead);

        tr = document.createElement('tr');
        thead.appendChild(tr);

        th = document.createElement('th');
        tr.appendChild(th);
        th.innerText = 'Produto';
        th.setAttribute('class', 'thProduto');
        th.setAttribute('colspan', '2');

        th = document.createElement('th');
        tr.appendChild(th);
        th.innerText = 'Preço';

        th = document.createElement('th');
        tr.appendChild(th);
        th.innerText = 'Quantidade';

        th = document.createElement('th');
        tr.appendChild(th);
        th.innerText = 'Total';
        th.setAttribute('colspan', '2');

        tbody = document.createElement('tbody');
        table.appendChild(tbody);

        for (var i = 0; i < itensCarrinho.produtos.length; i++)
        {
            tr = document.createElement('tr');
            tr.setAttribute('id', itensCarrinho['produtos'][i]['cproduto']);
            tr.setAttribute('class', 'itemCarrinho');
            tbody.appendChild(tr);

            td = document.createElement('td');
            td.setAttribute('class', 'tdImg');
            tr.appendChild(td);

            dvImg = document.createElement('div');
            dvImg.setAttribute('class', 'divProdutoCar');
            td.appendChild(dvImg);

            img = document.createElement('img');
            img.setAttribute('class', 'fotoCarrinho');
            img.src = itensCarrinho['produtos'][i]['foto'];
            dvImg.appendChild(img);

            td = document.createElement('td');
            td.setAttribute('class', 'tdPprod');
            tr.appendChild(td);
            td.innerText += itensCarrinho['produtos'][i]['categoria'];
            td.appendChild(document.createElement('br'));
            td.innerText += itensCarrinho['produtos'][i]['descricao'];

            td = document.createElement('td');
            td.setAttribute('class', 'tdPreco');
            tr.appendChild(td);
            td.innerText = itensCarrinho['produtos'][i]['preco'];

            td = document.createElement('td');
            tr.appendChild(td);
            td.setAttribute('class', 'tdQtde');

            inpt = document.createElement('input');
            td.appendChild(inpt);
            inpt.setAttribute('class', 'qtdeCompra');
            inpt.setAttribute('type', 'number');
            inpt.setAttribute('value', itensCarrinho['produtos'][i]['qtde']);

            td = document.createElement('td');
            tr.appendChild(td);
            td.innerText = "R$ " + (parseFloat(itensCarrinho['produtos'][i]['preco'].substring(3).replace(",", "."))
                    * parseFloat(itensCarrinho['produtos'][i]['qtde'])).toFixed(2).replace(".", ",") + " ";

            td = document.createElement('td');
            tr.appendChild(td);
            spn = document.createElement('span');
            td.appendChild(spn);
            spn.setAttribute('class', 'removerPedido');

            iaws = document.createElement('i');
            spn.appendChild(iaws);
            iaws.setAttribute('class', 'fas fa-times');

            cspn = document.createElement('span');
            tr.appendChild(cspn);
            cspn.style.display = 'none';
            cspn.id = 'cproduto';
            cspn.innerText = itensCarrinho['produtos'][i]['cproduto'];

            subtotal = parseFloat(subtotal)
                    + (parseFloat(itensCarrinho['produtos'][i]['preco'].substring(3).replace(",", "."))
                            * parseFloat(itensCarrinho['produtos'][i]['qtde']));
        }

        tr = document.createElement('tr');
        tr.setAttribute('class', 'totalizadorCarrinho');
        tbody.appendChild(tr);

        td = document.createElement('td');
        tr.appendChild(td);
        td.innerText = (logon ? 'Endereço de Entrega:' : 'Simule o Frete:');
        td.setAttribute('colspan', '3');

        td = document.createElement('td');
        tr.appendChild(td);
        td.innerText = 'Subtotal';

        td = document.createElement('td');
        tr.appendChild(td);
        td.setAttribute('colspan', '2');
        td.setAttribute('class', 'subtotalCarrinho');
        td.innerText = 'R$ ' + subtotal.toFixed(2).replace(".", ",");

        tr = document.createElement('tr');
        tr.setAttribute('class', 'totalizadorCarrinho');
        tbody.appendChild(tr);

        td = document.createElement('td');
        tr.appendChild(td);
        td.setAttribute('class', 'endPadraoCep');
        td.innerText = (logon ? 'Endereço Padrão' : 'Informe o CEP');

        td = document.createElement('td');
        input = document.createElement('input');
        input.setAttribute('type', 'number');
        input.setAttribute('id', 'CEPC');
        input.setAttribute('class', 'campoCep');
        td.appendChild(input);

        dvCalcular = document.createElement('div');
        dvCalcular.setAttribute('id', 'calcularFreteCarrinho');
        dvCalcular.innerText = 'Calcular';
        td.setAttribute('colspan', '2');
        td.appendChild(dvCalcular);
        tr.appendChild(td);
        if (logon)
        {
            dvCalcular.style.display = 'none';
            dvCalcular = document.createElement('div');
            dvCalcular.setAttribute('id', 'alterarEnd');
            dvCalcular.innerText = 'Alterar Endereço';
            td.appendChild(dvCalcular);
        }

        td = document.createElement('td');
        tr.appendChild(td);
        td.setAttribute('class', 'subtotalLinha');
        td.innerText = 'Frete';

        td = document.createElement('td');
        tr.appendChild(td);
        td.setAttribute('colspan', '2');
        td.setAttribute('class', 'subtotalLinha');
        td.setAttribute('id', 'freteCarrinho');
        td.innerText = '-';

        tr = document.createElement('tr');
        tr.setAttribute('class', 'totalizadorCarrinho');
        tbody.appendChild(tr);

        td = document.createElement('td');
        tr.appendChild(td);

        td = document.createElement('td');
        td.setAttribute('class', 'cidadeUf');
        td.setAttribute('colspan', '2');
        tr.appendChild(td);

        if (logon)
        {
            requisicaoHTTP("projetoPratico", "venda", "consultarEndPadrao", function (end) {
                document.querySelector("#CEPC").setAttribute("value", end);
                calcularFreteCarrinho();
            }, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]);
        }

        td = document.createElement('td');
        tr.appendChild(td);
        td.innerText = 'Total';

        td = document.createElement('td');
        tr.appendChild(td);
        td.setAttribute('colspan', '2');
        td.setAttribute('class', 'totalCarrinho');
        td.innerText = 'R$ ' + subtotal.toFixed(2).replace(".", ",");

        tr = document.createElement('tr');
        tr.setAttribute('class', 'totalizadorCarrinho');
        tbody.appendChild(tr);

        td = document.createElement('td');
        td.innerText = 'Data Entrega:';
        tr.appendChild(td);

        td = document.createElement('td');
        inpt = document.createElement('input');
        inpt.setAttribute('id', 'dataEntrega');
        inpt.setAttribute('type', 'date');
        td.appendChild(inpt);
        tr.appendChild(td);
        td.setAttribute('colspan', '5');

        dvFinalizar = document.createElement('div');
        dvFinalizar.setAttribute('id', 'finalizarCompra');
        dv.appendChild(dvFinalizar);
        dvFinalizar.innerText = 'Finalizar Compra';

        arr = document.querySelectorAll('.removerPedido');
        for (var j = 0; j < arr.length; j++) {
            arr[j].addEventListener('click', removerItem);
        }

    } else {

        dv = document.createElement('div');
        dv.setAttribute('class', 'carrinhoVazio');
        divCarrinho.appendChild(dv);

        hUm = document.createElement('h1');
        hUm.innerText = 'Seu Carrinho está vazio!';
        hUm.setAttribute('class', 'carrinhoVazioText');
        dv.appendChild(hUm);
    }

    document.querySelector('.atualizaCarrinho').addEventListener('click', listarCarrinho);
    if (logon)
    {
        document.querySelector('#alterarEnd').addEventListener('click', alterarEnd);
    } else {
        document.querySelector('#calcularFreteCarrinho').addEventListener('click', calcularFreteCarrinho);
    }

    var arrr = document.querySelectorAll(".qtdeCompra");
    for (var k = 0; k < arrr.length; k++)
    {
        arrr[k].addEventListener("blur", atualizarQtde);
    }

    document.querySelector('#finalizarCompra').addEventListener('click', escolherFormaPag);
    document.querySelector("#CEPC").end = 0;
}

function alterarEnd() {
    var logon = {};
    logon = window.localStorage.getItem("logon");
    logon = JSON.parse(logon);

    var divPrincipal, divSuperior, input, br, label, button, form, divBack;

    divBack = document.createElement("div");
    document.querySelector("article").appendChild(divBack);
    divBack.setAttribute("id", "divBack");

    divPrincipal = document.createElement("div");
    document.querySelector("article").appendChild(divPrincipal);
    divPrincipal.setAttribute("id", "divAlteraEnds");

    divSuperior = document.createElement("div");
    divPrincipal.appendChild(divSuperior);
    divSuperior.setAttribute("id", "divAlteraEndsSup");
    divSuperior.innerText = "Selecione um endereço:";

    requisicaoHTTP("projetoPratico", "venda", "consultarTodosEnds", function (end) {
        if (end["linhas"].length > 0)
        {
            form = document.createElement("form");
            divPrincipal.appendChild(form);

            for (var i = 0; i < end["linhas"].length; i++)
            {
                input = document.createElement("input");
                input.setAttribute('class', "radioEnd");
                input.setAttribute('id', end["linhas"][i]["PADRAO"]);
                input.setAttribute('type', "radio");
                input.setAttribute('name', "end");
                input.setAttribute('value', end["linhas"][i]["CEP"]);
                form.appendChild(input);

                label = document.createElement("label");
                label.innerText = end["linhas"][i]["CEP"] + " - " + end["linhas"][i]["CIDADE"] + " / " + end["linhas"][i]["UF"];
                form.appendChild(label);

                br = document.createElement("br");
                form.appendChild(br);
            }

            button = document.createElement("div");
            divPrincipal.appendChild(button);
            button.setAttribute('class', "btnOk");
            button.innerText = "OK";
            button.addEventListener("click", selecionarEnd);
        }

    }, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]);
}

function selecionarEnd(e) {

    var end = document.getElementsByName("end");
    var selecionado, cEndereco;

    for (var i = 0; i < end.length; i++) {
        if (end[i].checked) {
            selecionado = end[i].value;
            cEndereco = end[i].id;
        }
    }

    document.querySelector("#CEPC").setAttribute("value", selecionado);
    document.querySelector("#CEPC").end = cEndereco;
    calcularFreteCarrinho();

    e.target.parentNode.parentNode.removeChild(document.querySelector("#divBack"));
    e.target.parentNode.parentNode.removeChild(document.querySelector("#divAlteraEnds"));
}

function removerItem(e) {
    var itensCarrinho = {};
    itensCarrinho = window.localStorage.getItem('carrinho');
    itensCarrinho = JSON.parse(itensCarrinho);

    var item = e.target.parentNode.parentNode.parentNode;

    var cRemove;

    var cproduto = item.querySelector('#cproduto').innerHTML;

    for (var i = 0; i < itensCarrinho.produtos.length; i++) {
        if (cproduto === itensCarrinho['produtos'][i]['cproduto']) {
            cRemove = cproduto;
            var prodRemovido = itensCarrinho.produtos.find(function (obj, idx, arr) {
                if (obj.cproduto === cRemove) {
                    arr.splice(idx, 1);
                    return true;
                } else {
                    return false;
                }
            });
        }
    }
    window.localStorage.setItem('carrinho', JSON.stringify(itensCarrinho));
    irCarrinho();
}

function atualizarQtde(e) {
    var qtde = e.target.value;

    var itensCarrinho = {};
    itensCarrinho = window.localStorage.getItem('carrinho');
    itensCarrinho = JSON.parse(itensCarrinho);

    var item = e.target.parentNode.parentNode;

    var cAtualizaQtde;

    var cproduto = item.querySelector('#cproduto').innerHTML;

    for (var i = 0; i < itensCarrinho.produtos.length; i++) {
        if (cproduto === itensCarrinho['produtos'][i]['cproduto']) {
            cAtualizaQtde = cproduto;
            itensCarrinho.produtos.find(function (obj, idx, arr) {
                if (obj.cproduto === cAtualizaQtde) {
                    arr[idx].qtde = qtde;
                    return true;
                } else {
                    return false;
                }
            });
        }
    }
    window.localStorage.setItem('carrinho', JSON.stringify(itensCarrinho));
    irCarrinho();
}

function calcularFreteCarrinho() {
    var http = new XMLHttpRequest();
    var cep = document.querySelector("#CEPC").value.trim();

    if (cep.length === 8) {
        http.open('GET', 'https://viacep.com.br/ws/' + cep + '/json/', true);
        http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        http.addEventListener('load', function () {
            if (http.status === 200) {
                var dados = JSON.parse(http.response);
                if (dados.erro) {
                    document.querySelector(".cidadeUf").innerText = "";
                    document.querySelector("#freteCarrinho").innerText = "-";
                    document.querySelector(".totalCarrinho").innerText = document.querySelector(".subtotalCarrinho").innerText;
                    alert("CEP inválido! O CEP informado não existe!");
                } else {
                    verificarCidadeFrete(dados.localidade, dados.uf);
                }
            }
        });
        http.send(null);
    } else if (cep.length > 0) {
        document.querySelector(".cidadeUf").innerText = "";
        document.querySelector("#freteCarrinho").innerText = "-";
        document.querySelector(".totalCarrinho").innerText = document.querySelector(".subtotalCarrinho").innerText;
        alert("CEP inválido! Informe uma CEP de 8 números!");
    } else {
        document.querySelector(".cidadeUf").innerText = "";
        document.querySelector("#freteCarrinho").innerText = "-";
        document.querySelector(".totalCarrinho").innerText = document.querySelector(".subtotalCarrinho").innerText;
    }
}

function verificarCidadeFrete(cidade, uf) {
    document.querySelector('#calcularFreteCarrinho').cidadeUf = cidade + " - " + uf;
    requisicaoHTTP("projetoPratico", "venda", "verificaCidadeFrete", setarCidadeFrete, alert, "&CIDADE=" + removeAcento(cidade));
}

function setarCidadeFrete(cidadeFrete) {
    if (cidadeFrete !== null) {
        if (cidadeFrete["S"])
        {
            document.querySelector(".cidadeUf").innerText = document.querySelector('#calcularFreteCarrinho').cidadeUf;
            document.querySelector('#calcularFreteCarrinho').cidadeUf = undefined;
            if (cidadeFrete["S"] === "0.00")
            {
                document.querySelector("#freteCarrinho").innerText = "Grátis";
                document.querySelector(".totalCarrinho").innerText = document.querySelector(".subtotalCarrinho").innerText;
            } else {
                document.querySelector("#freteCarrinho").innerText = "R$ " + parseFloat(cidadeFrete["S"]).toFixed(2).replace('.', ',');
                document.querySelector(".totalCarrinho").innerText = "R$ " + (parseFloat(cidadeFrete["S"]) +
                        parseFloat(document.querySelector(".subtotalCarrinho").innerText.replace(',', '.').substring(3))).toFixed(2).replace('.', ',');
            }
        } else {
            document.querySelector(".cidadeUf").innerText = cidadeFrete["N"];
            document.querySelector("#freteCarrinho").innerText = "-";
            document.querySelector(".totalCarrinho").innerText = document.querySelector(".subtotalCarrinho").innerText;
        }
    }
}

function escolherFormaPag() {
    var logon = {};
    logon = window.localStorage.getItem("logon");
    logon = JSON.parse(logon);

    if (logon)
    {
        if (document.querySelector('#dataEntrega').value)
        {
            var itens = [];
            itens = window.localStorage.getItem('carrinho');
            itens = JSON.parse(itens);

            var todosItens = [];

            for (var i = 0; i < itens['produtos'].length; i++) {
                todosItens.push({CPRODUTO: parseInt(itens['produtos'][i]['cproduto']),
                    QTDE: itens['produtos'][i]['qtde']});
            }

            requisicaoHTTP("projetoPratico", "venda", "validaSaldo", function () {
                setInvisible();
                document.querySelector(".boxFormasPag").style.display = "block";
                setVisible(".boxFormasPag");

                document.querySelector("#boleto").addEventListener("click", finalizarBoleto);
                document.querySelector("#pagSeguro").addEventListener("click", finalizarPagSeguro);
            }, alert, "&ITENS=" + encodeURIComponent(JSON.stringify(todosItens))
                    + "&DTPREV=" + document.querySelector('#dataEntrega').value.split("-").reverse().join("/"));
        } else {
            alert("Preencha a data de entrega!");
        }
    } else {
        alert("Para finalizar a compra você precisa estar logado!");
        verificarLogin();
    }
}

function finalizarBoleto() {
    if (confirm('Deseja finalizar a compra?'))
    {
        var logon = {};
        logon = window.localStorage.getItem("logon");
        logon = JSON.parse(logon);

        var itens = [];
        itens = window.localStorage.getItem('carrinho');
        itens = JSON.parse(itens);

        var todosItens = [];
        var frete = document.querySelector('#freteCarrinho').innerText.replace(',', '.').substring(3);

        for (var i = 0; i < itens['produtos'].length; i++) {
            todosItens.push({CPRODUTO: parseInt(itens['produtos'][i]['cproduto']),
                PRECO: parseFloat(itens['produtos'][i]['preco'].replace(',', '.').replace('R$', '').trim()),
                QTDE: itens['produtos'][i]['qtde']});
        }
        requisicaoHTTP("projetoPratico", "venda", "inserirPedido", compraConcluida, alert, "&CCLIFOR=" + logon["cliente"][0]["codigo"]
                + "&ITENS=" + encodeURIComponent(JSON.stringify(todosItens))
                + "&FRETE=" + (frete.length > 0 && !isNaN(frete) ? frete : 0)
                + "&DTPREV=" + document.querySelector('#dataEntrega').value.split("-").reverse().join("/"));
    }
}

function finalizarPagSeguro() {
    requisicaoHTTP('SmartCityPagSeguro', 'SCRequest', 'getIdSession', function (data) {
        var hash;

        PagSeguroDirectPayment.setSessionId(data);

        PagSeguroDirectPayment.getPaymentMethods({
            amount: parseFloat(document.querySelector(".totalCarrinho").innerText.replace(',', '.').substring(3)),
            success: function (response) {
                document.querySelector('#bancoBrasilPS').src = 'https://stc.pagseguro.uol.com.br/' + response.paymentMethods.ONLINE_DEBIT.options.BANCO_BRASIL.images.MEDIUM.path;
                document.querySelector('#bancoBrasilPS').name = response.paymentMethods.ONLINE_DEBIT.options.BANCO_BRASIL.name;
                document.querySelector('#visaPS').src = 'https://stc.pagseguro.uol.com.br/' + response.paymentMethods.CREDIT_CARD.options.VISA.images.MEDIUM.path;
                document.querySelector('#visaPS').name = response.paymentMethods.CREDIT_CARD.options.VISA.name;

                setInvisible();
                document.querySelector(".boxPS").style.display = "block";
                setVisible(".boxPS");

                document.querySelector('#bancoBrasilPS').addEventListener("click", finalizarDebitoPS);
                document.querySelector('#visaPS').addEventListener("click", getDadosPagSeguro);
            },
            error: function (response) {
                console.log(response);
            }
        });

        PagSeguroDirectPayment.onSenderHashReady(function (response) {
            if (response.status === 'error') {
                console.log(response.message);
                return false;
            }
            document.querySelector("#CONFIRMPG").hash = response.senderHash;
        });

    }, function (erro) {
        console.log(erro);
        return;
    }, '&EMAIL=lw005973@cfjl.com.br&TOKEN=7B94AE2148D147CBABC8E52D7068C191', false, false);
}

function finalizarDebitoPS() {
    if (confirm('Deseja finalizar a compra?'))
    {
        var logon = {};
        logon = window.localStorage.getItem("logon");
        logon = JSON.parse(logon);

        var frete = document.querySelector('#freteCarrinho').innerText.replace(',', '.').substring(3);

        var params = [];
        params.push('email=lw005973@cfjl.com.br');
        params.push('token=7B94AE2148D147CBABC8E52D7068C191');

        params.push('paymentMode=default');
        params.push('paymentMethod=eft');
        params.push('currency=BRL');

        params.push('senderHash=' + document.querySelector("#CONFIRMPG").hash);
        params.push('senderName=PIER 95');
        params.push('senderEmail=v12020968474721906225@sandbox.pagseguro.com.br');
        params.push('senderAreaCode=55');
        params.push('senderPhone=999999999');
        params.push('senderCPF=94310067077');

        params.push('shippingAddressRequired=TRUE');
        params.push('shippingAddressStreet=' + '');
        params.push('shippingAddressNumber=' + '');
        params.push('shippingAddressDistrict=' + '');
        params.push('shippingAddressCity=' + '');
        params.push('shippingAddressState=' + '');
        params.push('shippingAddressCountry=' + 'BRA');
        params.push('shippingAddressPostalCode=' + '');
        params.push('shippingAddressComplement=' + '');
        params.push('reference=' + '');
        params.push('shippingCost=' + frete);
        params.push('shippingType=3');

        params.push('bankName=' + document.querySelector('#bancoBrasilPS').name);

        var itens = [];
        itens = window.localStorage.getItem('carrinho');
        itens = JSON.parse(itens);

        var todosItens = [];


        for (var i = 0; i < itens['produtos'].length; i++) {
            todosItens.push({CPRODUTO: parseInt(itens['produtos'][i]['cproduto']),
                DESCRICAO: itens['produtos'][i]['descricao'],
                PRECO: parseFloat(itens['produtos'][i]['preco'].replace(',', '.').replace('R$', '').trim()),
                QTDE: itens['produtos'][i]['qtde']});
        }

        requisicaoHTTP('projetoPratico', 'venda', 'goTransaction', function (data) {
            var ret = data;
            if (ret.includes && ret.includes('erro:')) {
                console.log(ret);
            } else {
                document.querySelector("#CONFIRMPG").hash = "";
                document.querySelector("#CEPC").end = "";
                document.querySelector("#CONFIRMPG").brand = "";
                compraConcluida('Transação Efetuada com Sucesso!\n' + ret.xStatus);
            }
        }, alert, '&' + params.join('&') + '&TIPOPAGAMENTO=' + parseInt(2) + '&JSONITENS='
                + encodeURIComponent(JSON.stringify(todosItens)) + '&TOTALTRANSACAO='
                + document.querySelector('.totalCarrinho').innerText.replace(',', '.').substring(3)
                + '&CCLIFOR=' + logon["cliente"][0]["codigo"] + '&CENDERECO=' + document.querySelector("#CEPC").end
                + "&DTPREV=" + document.querySelector('#dataEntrega').value.split("-").reverse().join("/"), false);
    }
}

function getDadosPagSeguro() {
    var campos = document.querySelector(".boxCreditCardPS").querySelectorAll(".camposMeusDados");
    for (var i = 0; i < campos.length; i++)
    {
        campos[i].style.backgroundColor = "rgba(239, 239, 239, 0.05)";
    }

    setInvisible();
    document.querySelector(".boxCreditCardPS").style.display = "block";
    setVisible(".boxCreditCardPS");

    document.querySelector("#CARDNUMBERPS").addEventListener("blur", obterParcelas);
    document.querySelector("#CONFIRMPG").addEventListener("click", obterTokenCartao);
}

function obterParcelas() {
    var parcelas;

    PagSeguroDirectPayment.getBrand({
        cardBin: document.querySelector("#CARDNUMBERPS").value.substring(0, 6), //6 primeiros dígitos do cartão
        success: function (response) {
            document.querySelector("#CONFIRMPG").brand = response.brand.name;
            PagSeguroDirectPayment.getInstallments({
                amount: parseFloat(document.querySelector(".totalCarrinho").innerText.replace(',', '.').substring(3)),
                maxInstallmentNoInterest: 12,
                brand: response.brand.name,
                success: function (response) {
                    parcelas = response.installments.visa;
                    var combo = document.querySelector("#cbParcelas");

                    if (document.querySelector("#cbParcelas").querySelector('option'))
                    {
                        do
                            combo.removeChild(document.querySelector("#cbParcelas").querySelector('option'));
                        while (combo.childNodes.length > 0)
                    }

                    var opt;
                    for (var i = 0; i < parcelas.length; i++)
                    {
                        opt = document.createElement('option');
                        opt.setAttribute('value', parcelas[i].quantity);
                        opt.text = parcelas[i].quantity + " parcela(s) de R$ " + parcelas[i].installmentAmount.toFixed(2).replace('.', ',');
                        combo.appendChild(opt);
                    }

                },
                error: function (response) {
                    console.log(response);
                }
            });
        },
        error: function (response) {
            console.log(response);
        }
    });
}

function obterTokenCartao() {
    var cardToken;

    PagSeguroDirectPayment.createCardToken({
        cardNumber: document.querySelector("#CARDNUMBERPS").value, // Número do cartão de crédito - 4111111111111111
        brand: document.querySelector("#CONFIRMPG").brand, // Bandeira do cartão - visa
        cvv: document.querySelector("#CVVPS").value, // CVV do cartão - 013
        expirationMonth: document.querySelector("#MESPS").value, // Mês da expiração do cartão - 12
        expirationYear: document.querySelector("#ANOPS").value, // Ano da expiração do cartão, é necessário os 4 dígitos. - 2026
        success: function (response) {
            cardToken = response.card.token;
            gotTransaction(cardToken);
        },
        error: function (response) {
            console.log(response);
        }
    });

}

function gotTransaction(cardToken) {
    var logon = {};
    logon = window.localStorage.getItem("logon");
    logon = JSON.parse(logon);

    var frete = document.querySelector('#freteCarrinho').innerText.replace(',', '.').substring(3);

    var params = [];
    params.push('email=lw005973@cfjl.com.br');
    params.push('token=7B94AE2148D147CBABC8E52D7068C191');

    params.push('paymentMode=default');
    params.push('paymentMethod=creditcard');
    params.push('currency=BRL');

    params.push('senderHash=' + document.querySelector("#CONFIRMPG").hash);
    params.push('senderName=PIER 95');
    params.push('senderEmail=v12020968474721906225@sandbox.pagseguro.com.br');
    params.push('senderAreaCode=55');
    params.push('senderPhone=999999999');
    params.push('senderCPF=94310067077');

    params.push('creditCardToken=' + cardToken);

    var index = document.querySelector("#cbParcelas").selectedIndex;
    var parc = document.querySelector("#cbParcelas").querySelectorAll('option')[index].value;
    var qtde = document.querySelector("#cbParcelas").querySelectorAll('option')[index].text.split("$ ")[1].replace(",", ".");

    params.push('installmentQuantity=' + parc);
    params.push('installmentValue=' + qtde);
    params.push('noInterestInstallmentQuantity=12');

    params.push('creditCardHolderName=PIER 95');
    params.push('creditCardHolderCPF=94310067077');
    params.push('creditCardHolderBirthDate=03/03/1990');
    params.push('creditCardHolderAreaCode=55');
    params.push('creditCardHolderPhone=999999999');

    params.push('billingAddressStreet=RUA PRINCIPAL');
    params.push('billingAddressNumber=123');
    params.push('billingAddressDistrict=CENTRO');
    params.push('billingAddressCity=NOVO MACHADO');
    params.push('billingAddressState=RS');
    params.push('billingAddressCountry=BRA');
    params.push('billingAddressPostalCode=98955000');
    params.push('billingAddressComplement=NAO');

    params.push('shippingAddressRequired=TRUE');
    params.push('shippingAddressStreet=' + '');
    params.push('shippingAddressNumber=' + '');
    params.push('shippingAddressDistrict=' + '');
    params.push('shippingAddressCity=' + '');
    params.push('shippingAddressState=' + '');
    params.push('shippingAddressCountry=' + 'BRA');
    params.push('shippingAddressPostalCode=' + '');
    params.push('shippingAddressComplement=' + '');
    params.push('reference=' + '');
    params.push('shippingCost=' + frete);
    params.push('shippingType=3');


    var itens = [];
    itens = window.localStorage.getItem('carrinho');
    itens = JSON.parse(itens);

    var todosItens = [];


    for (var i = 0; i < itens['produtos'].length; i++) {
        todosItens.push({CPRODUTO: parseInt(itens['produtos'][i]['cproduto']),
            DESCRICAO: itens['produtos'][i]['descricao'],
            PRECO: parseFloat(itens['produtos'][i]['preco'].replace(',', '.').replace('R$', '').trim()),
            QTDE: itens['produtos'][i]['qtde']});
    }

    requisicaoHTTP('projetoPratico', 'venda', 'goTransaction', function (data) {
        var ret = data;
        if (ret.includes && ret.includes('erro:')) {
            console.log(ret);
        } else {
            document.querySelector("#CONFIRMPG").hash = "";
            document.querySelector("#CEPC").end = "";
            document.querySelector("#CONFIRMPG").brand = "";
            compraConcluida('Transação Efetuada com Sucesso!\n' + ret.xStatus);
        }
    }, alert, '&' + params.join('&') + '&TIPOPAGAMENTO=' + parseInt(1) + '&JSONITENS='
            + encodeURIComponent(JSON.stringify(todosItens)) + '&TOTALTRANSACAO='
            + document.querySelector('.totalCarrinho').innerText.replace(',', '.').substring(3)
            + '&CCLIFOR=' + logon["cliente"][0]["codigo"] + '&CENDERECO=' + document.querySelector("#CEPC").end
            + "&DTPREV=" + document.querySelector('#dataEntrega').value.split("-").reverse().join("/")
            + '&CARDNUMER=' + document.querySelector("#CARDNUMBERPS").value, false);

}


function compraConcluida(data) {
    alert(data);
    localStorage.removeItem("carrinho");
    listarCarrinho();
}

init();