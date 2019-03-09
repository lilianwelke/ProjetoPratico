function init() {
    document.querySelector("#imgHeader").addEventListener("click", irInicio);
    document.querySelector("#h1Header").addEventListener("click", irInicio);
    document.querySelector("#h2Header").addEventListener("click", irInicio);
    document.querySelector("#conhecaPeixes").addEventListener("click", irPeixes);
}

var indice = 1;

function irInicio() {
    document.querySelector(".fundoInicio").style.display = "block";

    document.querySelector(".fundoInicio").style.zIndex = indice++;
    indice = indice++;

    document.querySelector(".divMenu").style.display = "none";
    document.querySelector(".listaProdutos").style.display = "none";
}

function irPeixes() {
    document.querySelector(".fundoInicio").style.display = "none";
    document.querySelector(".divMenu").style.display = "block";
    document.querySelector(".listaProdutos").style.display = "block";

    listarTodos();
}

function listarTodos() {
    requisicaoHTTP("projetoPratico", "produtos", "listarProdutos", listarTodosProdutos, alert, "&CSUBGRUPO=31");

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

        var reader = new FileReader();
        reader.readAsDataURL("/tecnicon/arquivos/imagem-produto/62.jpg".files[0]);
        reader.onload = function () {
            console.log(reader.result);
        };

        img = document.createElement('img');
        img.src = "/tecnicon/arquivos/imagem-produto//62.JPG";//produtos[i]['IMAGEM'];
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
}





init();