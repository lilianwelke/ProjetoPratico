function init() {
    document.querySelector("#imgHeader").addEventListener("click", irInicio);
    document.querySelector("#h1Header").addEventListener("click", irInicio);
    document.querySelector("#h2Header").addEventListener("click", irInicio);
    document.querySelector("#conhecaPeixes").addEventListener("click", irPeixes);
    document.querySelector(".lupa").addEventListener("click", pesquisarProdutos);
}

function irInicio() {
    document.querySelector(".fundoInicio").style.display = "block";
    document.querySelector(".divMenu").style.display = "none";
    document.querySelector(".listaProdutos").style.display = "none";
    document.querySelector(".divSobre").style.display = "none";
}

function irPeixes(pesquisa) {
    document.querySelector(".fundoInicio").style.display = "none";
    document.querySelector(".divSobre").style.display = "none";
    document.querySelector(".produtoMais").style.display = "none";
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
    document.querySelector(".produtoMais").style.display = "block";
    if (cproduto.trim() !== "") {
        requisicaoHTTP("projetoPratico", "produtos", "retornarProduto", listarProduto, alert, "&CPRODUTO=" + cproduto);
    }
}

function listarTodosProdutos(produtos) {
    var dv, hUm, p, dvVerMais, dvComprarMini, img;
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
        dvVerMais.innerText = 'Ver+';
        dv.appendChild(dvVerMais);

        dvComprarMini = document.createElement('div');
        dvComprarMini.setAttribute('class', 'comprarMini');
        dvComprarMini.setAttribute('id', produtos[i]['CPRODUTO']);
        dvComprarMini.innerText = 'Comprar';
        dv.appendChild(dvComprarMini);
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
    p.innerText = produto[0]['CATEGORIA'];
    dv.appendChild(p);

    hUm = document.createElement('h1');
    hUm.innerText = produto[0]['MERCADORIA'];
    dv.appendChild(hUm);

    pp = document.createElement('p');
    pp.innerText = produto[0]['PRODUTO'];
    dv.appendChild(pp);

    ppp = document.createElement('p');
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
    dvComprarMais.innerText = 'Comprar';
    dv.appendChild(dvComprarMais);
}

init();