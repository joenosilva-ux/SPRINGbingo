(() => {
    "use strict";

    const form = document.querySelector("#formulario-bingo");
    if (!form) return;

    const quantidade = document.querySelector("#quantidade-cartelas");
    const grupoPremios = document.querySelector("#grupo-premios");
    const grupoPorPagina = document.querySelector("#grupo-por-pagina");
    const passoNomes = document.querySelector("#passo-nomes-premios");
    const numeroPassoArte = document.querySelector("#numero-passo-arte");
    const camposPremios = [...document.querySelectorAll("[data-premio-campo]")];
    const inputsPremios = [...document.querySelectorAll("[data-premio-input]")];
    const resumo = document.querySelector("#resumo-lote");
    const status = document.querySelector("#mensagem-status");
    const gerar = document.querySelector("#gerar-lote");
    const novosNumeros = document.querySelector("#novos-numeros");
    const imprimir = document.querySelector("#imprimir");
    const anterior = document.querySelector("#folha-anterior");
    const proxima = document.querySelector("#proxima-folha");
    const indicador = document.querySelector("#indicador-folha");
    const seletorArte = document.querySelector("#seletor-arte");
    const inputArte = document.querySelector("#arte-evento");
    const arteSelecionada = document.querySelector("#arte-selecionada");
    const miniaturaArte = document.querySelector("#miniatura-arte");
    const nomeArte = document.querySelector("#nome-arte");
    const removerArte = document.querySelector("#remover-arte");

    const folhaAtual = () => document.querySelector("#folha-atual");
    const valorMarcado = nome => form.querySelector(`input[name="${nome}"]:checked`).value;
    const modoAtual = () => valorMarcado("modo");
    const numeroCartelas = () => Number.parseInt(quantidade.value, 10);
    const numeroPremios = () => Number.parseInt(valorMarcado("quantidadePremios"), 10);
    const porPagina = () => Number.parseInt(valorMarcado("cartelasPorPagina"), 10);

    let loteId = folhaAtual().dataset.loteId;
    let totalFolhas = Number(folhaAtual().dataset.totalFolhas);
    let paginaAtual = Number(folhaAtual().dataset.paginaAtual);
    let arteAtual = null;
    let gerando = false;
    let regerarPendente = false;
    let temporizador = null;

    function configuracaoValida() {
        return Number.isInteger(numeroCartelas()) && numeroCartelas() >= 1 && numeroCartelas() <= 200;
    }

    function atualizarInterface() {
        const variosPremios = modoAtual() === "VARIOS_PREMIOS";
        grupoPremios.hidden = !variosPremios;
        grupoPorPagina.hidden = variosPremios;
        passoNomes.hidden = !variosPremios;
        numeroPassoArte.textContent = variosPremios ? "4" : "3";

        camposPremios.forEach(campo => {
            campo.hidden = !variosPremios || Number(campo.dataset.premioCampo) > numeroPremios();
        });
        atualizarResumo();
        aplicarTitulos(folhaAtual());
    }

    function atualizarResumo() {
        if (!configuracaoValida()) {
            resumo.textContent = "Informe uma quantidade entre 1 e 200.";
            return;
        }
        const total = numeroCartelas();
        if (modoAtual() === "VARIOS_PREMIOS") {
            const premios = numeroPremios();
            resumo.textContent = `${total} ${plural(total, "cartela diferente", "cartelas diferentes")} em `
                + `${total} ${plural(total, "folha A4", "folhas A4")}, com `
                + `${premios} ${plural(premios, "prêmio", "prêmios")} em cada folha.`;
        } else {
            const folhasCalculadas = Math.ceil(total / porPagina());
            resumo.textContent = `${total} ${plural(total, "cartela diferente", "cartelas diferentes")} em `
                + `${folhasCalculadas} ${plural(folhasCalculadas, "folha A4", "folhas A4")}, `
                + `com até ${porPagina()} por folha.`;
        }
    }

    function plural(numero, singular, pluralTexto) {
        return numero === 1 ? singular : pluralTexto;
    }

    function tituloPremio(numero) {
        const input = inputsPremios.find(item => Number(item.dataset.premioInput) === numero);
        return input?.value.trim() || `${numero}º prêmio`;
    }

    function aplicarTitulos(raiz) {
        raiz.querySelectorAll(".cartela-bloco[data-numero-premio]").forEach(bloco => {
            const numero = Number(bloco.dataset.numeroPremio);
            const titulo = bloco.querySelector(".titulo-premio");
            if (numero > 0 && titulo) titulo.textContent = tituloPremio(numero);
        });
    }

    function aplicarArte(raiz) {
        raiz.querySelectorAll(".folha-a4").forEach(folha => {
            folha.querySelector(".arte-evento-bloco")?.remove();
            const marca = folha.querySelector(".marca-dagua-evento");
            const quantidadeItens = Number(folha.dataset.itens);

            folha.classList.remove("com-arte", "com-marca-dagua");
            folha.classList.toggle("sem-arte", !arteAtual);
            marca.hidden = true;
            marca.removeAttribute("src");

            if (!arteAtual) return;

            if (quantidadeItens === 6) {
                marca.src = arteAtual;
                marca.hidden = false;
                folha.classList.add("com-marca-dagua");
                return;
            }

            const blocoArte = document.createElement("figure");
            blocoArte.className = "arte-evento-bloco";
            const imagem = document.createElement("img");
            imagem.src = arteAtual;
            imagem.alt = "Arte do evento";
            blocoArte.appendChild(imagem);
            folha.querySelector(".grade-cartelas").appendChild(blocoArte);
            folha.classList.add("com-arte");
        });
    }

    function parametrosConfiguracao() {
        const parametros = new URLSearchParams({
            modo: modoAtual(),
            quantidadeCartelas: String(numeroCartelas()),
            quantidadePremios: String(numeroPremios()),
            cartelasPorPagina: String(porPagina())
        });
        if (loteId) parametros.set("loteAnterior", loteId);
        return parametros;
    }

    async function criarLote(mensagem = "Gerando suas cartelas…", mostrarValidacao = false) {
        if (!configuracaoValida()) {
            if (mostrarValidacao) quantidade.reportValidity();
            return;
        }
        if (gerando) {
            regerarPendente = true;
            return;
        }

        gerando = true;
        definirOcupado(true, mensagem);
        try {
            const resposta = await fetch("/bingo/lotes", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                body: parametrosConfiguracao(),
                cache: "no-store"
            });
            const corpo = await resposta.text();
            if (!resposta.ok) throw new Error(extrairTexto(corpo) || "Não foi possível gerar as cartelas.");
            const lote = JSON.parse(corpo);
            loteId = lote.id;
            totalFolhas = lote.totalFolhas;
            paginaAtual = 1;
            await carregarPagina(1, false);
            status.textContent = "Prévia atualizada. Navegue pelas folhas ou imprima o lote completo.";
            status.classList.remove("erro");
        } catch (erro) {
            status.textContent = erro.message;
            status.classList.add("erro");
        } finally {
            gerando = false;
            definirOcupado(false);
            if (regerarPendente) {
                regerarPendente = false;
                criarLote();
            }
        }
    }

    async function carregarPagina(numero, controlarOcupado = true) {
        if (!loteId || numero < 1 || numero > totalFolhas) return;
        if (controlarOcupado) definirOcupado(true, `Carregando folha ${numero}…`);
        try {
            const resposta = await fetch(`/bingo/lotes/${encodeURIComponent(loteId)}/paginas/${numero}`, {
                cache: "no-store"
            });
            const html = await resposta.text();
            if (!resposta.ok) throw new Error(extrairTexto(html) || "Não foi possível carregar esta folha.");
            folhaAtual().innerHTML = html;
            paginaAtual = numero;
            folhaAtual().dataset.loteId = loteId;
            folhaAtual().dataset.totalFolhas = String(totalFolhas);
            folhaAtual().dataset.paginaAtual = String(paginaAtual);
            aplicarArte(folhaAtual());
            aplicarTitulos(folhaAtual());
            atualizarNavegacao();
            if (controlarOcupado) {
                status.textContent = `Folha ${paginaAtual} de ${totalFolhas}.`;
                status.classList.remove("erro");
            }
        } catch (erro) {
            status.textContent = erro.message;
            status.classList.add("erro");
        } finally {
            if (controlarOcupado) definirOcupado(false);
        }
    }

    async function imprimirLote() {
        if (!loteId || gerando) return;
        definirOcupado(true, "Preparando todas as folhas para impressão…");
        document.querySelector("#impressao-temporaria")?.remove();
        try {
            const resposta = await fetch(`/bingo/lotes/${encodeURIComponent(loteId)}/impressao`, {
                cache: "no-store"
            });
            const html = await resposta.text();
            if (!resposta.ok) throw new Error(extrairTexto(html) || "Não foi possível preparar a impressão.");

            const impressaoTemporaria = document.createElement("div");
            impressaoTemporaria.id = "impressao-temporaria";
            impressaoTemporaria.innerHTML = html;
            document.body.appendChild(impressaoTemporaria);
            aplicarArte(impressaoTemporaria);
            aplicarTitulos(impressaoTemporaria);

            const imagens = [...impressaoTemporaria.querySelectorAll("img[src]")];
            await Promise.all(imagens.map(imagem => imagem.decode().catch(() => undefined)));
            await new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve)));

            const limparImpressao = () => impressaoTemporaria.remove();
            window.addEventListener("afterprint", limparImpressao, { once: true });
            definirOcupado(false);
            window.print();
        } catch (erro) {
            document.querySelector("#impressao-temporaria")?.remove();
            status.textContent = erro.message;
            status.classList.add("erro");
            definirOcupado(false);
        }
    }

    function extrairTexto(html) {
        const documento = new DOMParser().parseFromString(html, "text/html");
        return documento.body.textContent.trim();
    }

    function definirOcupado(ocupado, mensagem = "") {
        folhaAtual().setAttribute("aria-busy", String(ocupado));
        gerar.disabled = ocupado;
        novosNumeros.disabled = ocupado;
        imprimir.disabled = ocupado;
        if (ocupado) {
            anterior.disabled = true;
            proxima.disabled = true;
        } else {
            atualizarNavegacao();
        }
        if (mensagem) {
            status.textContent = mensagem;
            status.classList.remove("erro");
        }
    }

    function atualizarNavegacao() {
        indicador.textContent = `Folha ${paginaAtual} de ${totalFolhas}`;
        anterior.disabled = paginaAtual <= 1;
        proxima.disabled = paginaAtual >= totalFolhas;
    }

    function agendarGeracao() {
        clearTimeout(temporizador);
        temporizador = setTimeout(() => criarLote(), 350);
    }

    function lerArte(arquivo) {
        if (!arquivo) return;
        const tipos = ["image/png", "image/jpeg", "image/webp"];
        if (!tipos.includes(arquivo.type) || arquivo.size > 8 * 1024 * 1024) {
            status.textContent = "Escolha uma imagem PNG, JPEG ou WebP de até 8 MB.";
            status.classList.add("erro");
            inputArte.value = "";
            return;
        }
        const leitor = new FileReader();
        leitor.onload = () => {
            arteAtual = leitor.result;
            miniaturaArte.src = arteAtual;
            nomeArte.textContent = arquivo.name;
            seletorArte.hidden = true;
            arteSelecionada.hidden = false;
            aplicarArte(folhaAtual());
            status.textContent = Number(folhaAtual().querySelector(".folha-a4")?.dataset.itens) === 6
                ? "Arte aplicada como marca-d’água suave."
                : "Arte aplicada ao centro superior da folha.";
            status.classList.remove("erro");
        };
        leitor.onerror = () => {
            status.textContent = "Não foi possível ler essa imagem. Tente outro arquivo.";
            status.classList.add("erro");
        };
        leitor.readAsDataURL(arquivo);
    }

    form.addEventListener("submit", evento => {
        evento.preventDefault();
        criarLote("Gerando suas cartelas…", true);
    });
    form.querySelectorAll('input[name="modo"], input[name="quantidadePremios"], input[name="cartelasPorPagina"]')
        .forEach(input => input.addEventListener("change", () => {
            atualizarInterface();
            agendarGeracao();
        }));
    quantidade.addEventListener("input", () => {
        atualizarResumo();
        agendarGeracao();
    });
    inputsPremios.forEach(input => input.addEventListener("input", () => aplicarTitulos(folhaAtual())));
    anterior.addEventListener("click", () => carregarPagina(paginaAtual - 1));
    proxima.addEventListener("click", () => carregarPagina(paginaAtual + 1));
    novosNumeros.addEventListener("click", () => criarLote("Sorteando outros números…"));
    imprimir.addEventListener("click", imprimirLote);
    seletorArte.addEventListener("click", evento => {
        if (evento.target !== inputArte) inputArte.click();
    });
    seletorArte.addEventListener("keydown", evento => {
        if (evento.key === "Enter" || evento.key === " ") inputArte.click();
    });
    inputArte.addEventListener("change", () => lerArte(inputArte.files[0]));
    ["dragenter", "dragover"].forEach(tipo => seletorArte.addEventListener(tipo, evento => {
        evento.preventDefault();
        seletorArte.classList.add("arrastando");
    }));
    ["dragleave", "drop"].forEach(tipo => seletorArte.addEventListener(tipo, evento => {
        evento.preventDefault();
        seletorArte.classList.remove("arrastando");
    }));
    seletorArte.addEventListener("drop", evento => lerArte(evento.dataTransfer.files[0]));
    removerArte.addEventListener("click", () => {
        arteAtual = null;
        inputArte.value = "";
        miniaturaArte.removeAttribute("src");
        seletorArte.hidden = false;
        arteSelecionada.hidden = true;
        aplicarArte(folhaAtual());
        status.textContent = "Arte removida.";
    });

    atualizarInterface();
    atualizarNavegacao();
    aplicarArte(folhaAtual());
})();
