function init() {
    document.querySelector("#conhecaGente").addEventListener("click", irSobre);
}

function irSobre() {
    setInvisible();
    document.querySelector(".divSobre").style.display = "block";
    setVisible(".divSobre");

}

init();