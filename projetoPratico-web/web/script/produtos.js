function init() {
    document.querySelector("#imgHeader").addEventListener("click", irInicio);
    document.querySelector("#h1Header").addEventListener("click", irInicio);
    document.querySelector("#h2Header").addEventListener("click", irInicio);
    document.querySelector("#conhecaPeixes").addEventListener("click", irPeixes);
    document.querySelector(".lupa").addEventListener("click", pesquisarProdutos);
    document.querySelector(".carrinho").addEventListener("click", irCarrinho);
}

function irInicio() {
    document.querySelector(".fundoInicio").style.display = "block";
    document.querySelector(".divMenu").style.display = "none";
    document.querySelector(".listaProdutos").style.display = "none";
    document.querySelector(".divSobre").style.display = "none";
    document.querySelector(".divCarrinho").style.display = "none";
    document.querySelector(".produtoMais").style.display = "none";
}

function irPeixes(pesquisa) {
    document.querySelector(".fundoInicio").style.display = "none";
    document.querySelector(".divSobre").style.display = "none";
    document.querySelector(".produtoMais").style.display = "none";
    document.querySelector(".divCarrinho").style.display = "none";
    document.querySelector(".divMenu").style.display = "block";
    document.querySelector(".listaProdutos").style.display = "block";

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
    var cproduto = e.target.id;
    document.querySelector(".fundoInicio").style.display = "none";
    document.querySelector(".divSobre").style.display = "none";
    document.querySelector(".divMenu").style.display = "none";
    document.querySelector(".listaProdutos").style.display = "none";
    document.querySelector(".divCarrinho").style.display = "none";
    document.querySelector(".produtoMais").style.display = "block";
    if (cproduto.trim() !== "") {
        requisicaoHTTP("projetoPratico", "produtos", "retornarProduto", listarProduto, alert, "&CPRODUTO=" + cproduto);
    }
}

function irCarrinho(e) {
    document.querySelector(".fundoInicio").style.display = "none";
    document.querySelector(".divSobre").style.display = "none";
    document.querySelector(".divMenu").style.display = "none";
    document.querySelector(".listaProdutos").style.display = "none";
    document.querySelector(".produtoMais").style.display = "none";
    document.querySelector(".divCarrinho").style.display = "block";

    listarCarrinho();
}

function listarTodosProdutos(produtos) {
    var dv, hUm, p, dvVerMais, img;
    var div = document.querySelector('.listaProdutos');
    div.innerHTML = "";

    for (var i = 0; i < produtos.length; i++)
    {
        dv = document.createElement('div');
        dv.setAttribute('class', 'produtoMini');
        div.appendChild(dv);

        img = document.createElement('img');
        img.src = 'data:image/jpg;base64,' + produtos[i]['IMAGEM'];
        img.setAttribute('class', 'fotoMini');
        dv.appendChild(img);

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
    var dv, hUm, p, pp, ppp, pppp, dvImg, dvComprarMais, img, input;
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
    input.setAttribute('value', '1');
    dv.appendChild(input);

    pppp = document.createElement('p');
    pppp.setAttribute('class', 'unidadeMais');
    pppp.innerText = produto[0]['UN'];
    dv.appendChild(pppp);

    dv.appendChild(document.createElement('br'));

    dvComprarMais = document.createElement('div');
    dvComprarMais.setAttribute('class', 'comprarMais');
    dvComprarMais.setAttribute('id', produto[0]['CPRODUTO']);
    dvComprarMais.innerText = 'Adicionar ao Carrinho';
    dv.appendChild(dvComprarMais);

    document.querySelector(".comprarMais").addEventListener("click", adicionarCarrinho);
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
}

function listarCarrinho() {
    var itensCarrinho = {};
    itensCarrinho = window.localStorage.getItem('carrinho');
    itensCarrinho = JSON.parse(itensCarrinho);

    document.querySelector('.divCarrinho').innerHTML = '';

    var divCarrinho = document.querySelector('.divCarrinho');
    var total = 0.0;
    var dv, table, thead, tbody, tr, th, td, dvImg, img, inpt, spn, iaws, cspn, hUmDiv, hUm, i, dvFinalizar, arr;

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
            td.innerText = itensCarrinho['produtos'][i]['descricao'];

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
            td.innerText = "R$ " + (parseFloat(itensCarrinho['produtos'][i]['preco'].substring(3))
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

            total = parseFloat(total)
                    + (parseFloat(itensCarrinho['produtos'][i]['preco'].substring(3)) * parseFloat(itensCarrinho['produtos'][i]['qtde']));
        }

        tr = document.createElement('tr');
        tr.setAttribute('class', 'totalizadorCarrinho');
        tbody.appendChild(tr);

        td = document.createElement('td');
        tr.appendChild(td);
        td.setAttribute('colspan', '4');

        td = document.createElement('td');
        tr.appendChild(td);
        td.setAttribute('colspan', '2');
        td.setAttribute('class', 'totalCarrinho');
        td.innerText = 'R$ ' + total.toFixed(2).replace(".", ",");

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
    document.querySelector('.qtdeCompra').addEventListener('blur', atualizarQtde);

    document.querySelector('#finalizarCompra').addEventListener('click', finalizarCompra);

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

    var item = e.target.parentNode.parentNode.parentNode;

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

function finalizarCompra() {

    requisicaoHTTP("projetoPratico", "venda", "inserirPedido", compraConcluida, alert, "&CCLIFOR=1434");


}

function compraConcluida() {
    alert("Compra OK!");
}

init();