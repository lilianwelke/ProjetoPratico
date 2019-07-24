function requisicaoHTTP(projeto, classe, metodo, funcaoOK, funcaoErro, parametros) {
    var http = new XMLHttpRequest();
    http.open('POST', 'http://portal.tecnicon.com.br:7078/TecniconPCHttp/ConexaoHttp?p=evento=ERPMetodos|sessao=|empresa=17|filial=1|local=1|parametro=' +
            'projeto=' + projeto + '|classe=' + classe + '|metodo=' + metodo + '|recurso=metadados' + parametros, true);

//    http.open('POST', 'http://192.168.1.196:7078/TecniconPCHttp/ConexaoHttp?p=evento=ERPMetodos|sessao=|empresa=17|filial=1|local=1|parametro=' +
//            'projeto=' + projeto + '|classe=' + classe + '|metodo=' + metodo + '|recurso=metadados' + parametros, true);

    http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    http.addEventListener('load', function () {
        if (http.status === 200) {
            var dados = xmlToJSON(http.responseXML);
            if (dados.erro) {
                funcaoErro(dados.erro);
            } else if (dados.result) {
                funcaoOK(dados.result);
            }
        }
    });
    http.send(null);
}

function xmlToJSON(XMLDocument) {
    var retorno = {result: XMLDocument.getElementsByTagName('result')[0].textContent,
        erro: XMLDocument.getElementsByTagName('erro')[0].textContent};
    try {
        retorno.result = JSON.parse(retorno.result);
    } catch (e) {
    }
    try {
        retorno.erro = JSON.parse(retorno.erro);
    } catch (e) {
    }
    return retorno;
}

function serializeForm(idForm, classCampos) {
    var arrCampos = document.querySelectorAll('#' + idForm + ' .' + classCampos);
    var arrParams = [], i, qtde;
    for (i = 0, qtde = arrCampos.length; i < qtde; i++) {
        arrParams.push(arrCampos[i].id + '=' + encodeURIComponent(arrCampos[i].value));
    }
    return '&' + arrParams.join('&');
}

function setVisible(classe) {
    var element = document.querySelector(classe);
    element.classList.add('setVisible');
    element.style.display = "block";
}

function setInvisible() {
    var ar = document.querySelectorAll(".setVisible");
    for (var k = 0; k < ar.length; k++)
    {
        ar[k].classList.remove('setVisible');
        ar[k].style.display = "none";
    }
}

function setLogado(classe) {
    document.querySelector(classe).classList.add('setLogado');
}

function setUnLogado(classe) {
    document.querySelector(classe).classList.add('setUnLogado');
}

function validarLogon() {
    if (localStorage.logon)
    {
        (document.querySelector(".setLogado") !== null ? document.querySelector(".divUser").classList.remove('setLogado') : "");
        (document.querySelector(".setUnLogado") !== null ? document.querySelector(".divLogado").classList.remove('setUnLogado') : "");
        setUnLogado(".divUser");
        setLogado(".divLogado");
    } else
    {
        (document.querySelector(".setLogado") !== null ? document.querySelector(".divLogado").classList.remove('setLogado') : "");
        (document.querySelector(".setUnLogado") !== null ? document.querySelector(".divUser").classList.remove('setUnLogado') : "");
        setUnLogado(".divLogado");
        setLogado(".divUser");
    }
}

function removeAcento(text)
{
    text = text.toLowerCase();
    text = text.replace(new RegExp('[ÁÀÂÃ]', 'gi'), 'a');
    text = text.replace(new RegExp('[ÉÈÊ]', 'gi'), 'e');
    text = text.replace(new RegExp('[ÍÌÎ]', 'gi'), 'i');
    text = text.replace(new RegExp('[ÓÒÔÕ]', 'gi'), 'o');
    text = text.replace(new RegExp('[ÚÙÛ]', 'gi'), 'u');
    text = text.replace(new RegExp('[Ç]', 'gi'), 'c');
    return text;
}