function init() {
    document.querySelector("#conhecaGente").addEventListener("click", irSobre);
}

function irSobre() {
    document.querySelector(".fundoInicio").style.display = "none";
    document.querySelector(".divMenu").style.display = "none";
    document.querySelector(".listaProdutos").style.display = "none";
    document.querySelector(".produtoMais").style.display = "none";
    document.querySelector(".divCarrinho").style.display = "none";
    document.querySelector(".divSobre").style.display = "block";
}

init();