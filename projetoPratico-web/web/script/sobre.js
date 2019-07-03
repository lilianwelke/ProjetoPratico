function init() {
    document.querySelector("#contateNos").addEventListener("click", irContato);
    document.querySelector("#conhecaGente").addEventListener("click", irSobre);
}

function irSobre() {
    setInvisible();
    document.querySelector(".divSobre").style.display = "block";
    setVisible(".divSobre");
}

function irContato() {
    setInvisible();
    document.querySelector(".divContato").style.display = "block";
    setVisible(".divContato");
}

init();