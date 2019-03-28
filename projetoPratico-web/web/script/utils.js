function requisicaoHTTP(projeto, classe, metodo, funcaoOK, funcaoErro, parametros) {
    var http = new XMLHttpRequest();
    http.open('POST', 'http://portal.tecnicon.com.br:7078/TecniconPCHttp/ConexaoHttp?p=evento=ERPMetodos|sessao=|empresa=|filial=|local=|parametro=' +
            'projeto=' + projeto + '|classe=' + classe + '|metodo=' + metodo + '|recurso=metadados' + parametros, true);

//    http.open('POST', 'http://192.168.1.196:7078/TecniconPCHttp/ConexaoHttp?p=evento=ERPMetodos|sessao=|empresa=|filial=|local=|parametro=' +
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