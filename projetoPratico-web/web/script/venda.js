PagSeguroECommerce.efetuarTransacao = function (tela) {
    buscaStorage();
    var jo = JSON.parse(tela.jsonInfo).info[0];
    var tipoPagamento = tela.querySelector('#tipoPagamento').value;

    var params = [];
    params.push('email=' + PagSeguroECommerce.getEmailAcesso(tela));
    params.push('token=' + PagSeguroECommerce.getToken(tela));
    params.push('paymentMode=default');
    params.push('paymentMethod=' + PagSeguroECommerce.metodoPagamento(tela));

    if (parseInt(tipoPagamento) === 3)
        params.push('bankName=' + tela.querySelector('#bancoDebito').value);

    params.push('receiverEmail=' + jo.receiverEmail);
    params.push('currency=' + jo.currency);
    params.push('extraAmount=' + parseDouble(jo.extraAmount));

    params.push('notificationURL=' + jo.notificationURL);
    params.push('reference=' + jo.reference);
    params.push('senderName=' + jo.senderName);
    params.push('senderCPF=' + jo.senderCPF);
    params.push('senderAreaCode=' + jo.senderAreaCode);
    params.push('senderPhone=' + jo.senderPhone);
    params.push('senderEmail=' + jo.senderEmail);
    params.push('senderHash=' + tela.senderHash);
    params.push('shippingAddressStreet=' + jo.shippingAddressStreet);
    params.push('shippingAddressNumber=' + jo.shippingAddressNumber);
    params.push('shippingAddressComplement=' + jo.shippingAddressComplement);
    params.push('shippingAddressDistrict=' + jo.shippingAddressDistrict);
    params.push('shippingAddressPostalCode=' + jo.shippingAddressPostalCode);
    params.push('shippingAddressCity=' + jo.shippingAddressCity);
    params.push('shippingAddressState=' + jo.shippingAddressState);
    params.push('shippingAddressCountry=' + jo.shippingAddressCountry);
    params.push('shippingType=' + jo.shippingType);
    params.push('shippingCost=' + jo.shippingCost);

    if (parseInt(tipoPagamento) === 1) {
        if (!tela.querySelector('#opcaoParcelamento').value) {
            alert("Por Favor selecione a forma de parcelamento antes de prosseguir!");
            return;
        }
        params.push('creditCardToken=' + tela.tokenCartao);
        params.push('installmentQuantity=' + tela.querySelector('#opcaoParcelamento').value.split('-')[0]);
        params.push('installmentValue=' + parseDouble(tela.querySelector('#opcaoParcelamento').value.split('-')[1]));
        params.push('noInterestInstallmentQuantity=12'); //tela.querySelector('#opcaoParcelamento').value.split('-')[0]
        params.push('creditCardHolderName=' + jo.senderName);
        params.push('creditCardHolderCPF=' + jo.senderCPF);
        params.push('creditCardHolderBirthDate=' + jo.birthDate);
        params.push('creditCardHolderAreaCode=' + jo.senderAreaCode);
        params.push('creditCardHolderPhone=' + jo.senderPhone);
        params.push('billingAddressStreet=' + jo.shippingAddressStreet);
        params.push('billingAddressNumber=' + jo.shippingAddressNumber);
        params.push('billingAddressComplement=' + jo.shippingAddressComplement);
        params.push('billingAddressDistrict=' + jo.shippingAddressDistrict);
        params.push('billingAddressPostalCode=' + jo.shippingAddressPostalCode);
        params.push('billingAddressCity=' + jo.shippingAddressCity);
        params.push('billingAddressState=' + jo.shippingAddressState);
        params.push('billingAddressCountry=' + jo.shippingAddressCountry);
    }
    var metodoPag = PagSeguroECommerce.metodoPagamento(tela);
    var cartao = encodeURIComponent(window.btoa(tela.querySelector("#numeroCartao").value));
    var codseg = encodeURIComponent(window.btoa(tela.querySelector("#codigoSeguranca").value));
    var mesVal = tela.querySelector("#mesValidade").value;
    var anoVal = tela.querySelector("#anoValidade").value;
    var prazo = '';
    var iscartao;
    switch (metodoPag) {
        case 'creditcard':
            if (!tela.querySelector('#opcaoParcelamento').value.split('-')[3]) {
                jAlert("Opção de Parcelamento não selecionada!", "Alerta!");
            }
            prazo = tela.querySelector('#opcaoParcelamento').value.split('-')[3];
            iscartao = 'ok';
            break;
        case 'boleto':
            prazo = "vista";
            break;
        case 'eft':
            prazo = "vista";
            break;
        default:
            prazo = "vista";
    }
    var jsonDadosProds = sessionStorage.getItem('_produtosCarrinho');
    var prods = JSON.parse(jsonDadosProds);
    var produtos = [];
    for (var c in prods) {
        produtos.push(c);
    }

    executaServico('SmartCityPagSeguro', 'SCRequest.goTransaction', null, function (data) {
        var ret = trim(data);
        if (ret.includes('erro:')) {
            alert(ret);
        } else {
            var joRet = JSON.parse(ret);
            alert('Transação Efetuada com Sucesso!\n' + joRet.xStatus);
            tela.STRANSACAO = joRet.STRANSACAO;
            if (parseInt(tipoPagamento) !== 1) {
                window.open(joRet.paymentLink);
            }
            executaServico('TecniconECommerce', 'Checkout.emitirPedido', function (erro) {
                alert(erro);
            }, function (data) {
                sessionStorage.setItem('_produtosCarrinho', '{}');
                executaServico("TecniconECommerce", "Carrinho.clearCarrinho", function (err) {
                    alert(err, "Alerta");
                }, function (data) {

                }, "&cclifor=" + configuracoes.cclifor + "&filialcf=" + configuracoes.filialcf);
                window.open("/Ecommerce/Login?pedido=true", "_self");
            }, '&cClifor=' + configuracoes.cclifor + '&filialCF=' + configuracoes.filialcf + '&prazoPgto=' + prazo + '&produtos=' + produtos.join(',') + '&jsonProd=' + encodeURIComponent(jsonDadosProds) + '&cartao=' + cartao + '&codseg=' + codseg + '&mesVal=' + mesVal + '&anoVal=' + anoVal + "&bandeira=" + tela.bandeiraCartao + "&totalTransacao=" + PagSeguroECommerce.getTotalTransacao(tela) + "&parcelas=" + tela.querySelector('#opcaoParcelamento').value.split('-')[0]);
        }
    }, '&' + params.join('&') + '&TIPOPAGAMENTO=' + parseInt(tipoPagamento) + '&JSONITENS=' + encodeURIComponent(tela.jsonItens) + '&TIPOAMBIENTE=' + PagSeguroECommerce.getTipoAmbiente(tela) + (!tela.STRANSACAO ? '' : '&STRANSACAO=' + tela.STRANSACAO) + '&TABELAORIGEM=' + "ECOMMERCE" + '&SEQORIGEM=' + "01" + '&TOTALTRANSACAO=' + tela.val + "&SHIPPINGCOST=" + '&CSCPESSOA=0', false);
};