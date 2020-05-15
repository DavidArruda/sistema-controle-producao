package br.com.project.report.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.util.JRLoader;

@Component
public class ReportUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String UNDERLINE = "_";
	private static final String FOLDER_RELATORIOS = "/relatorios";
	private static final String SUBREPORT_DIR = "SUBREPORT_DIR";
	private static final String EXTESION_ODS = "ods";
	private static final String EXTESION_XLS = "xls";
	private static final String EXTESION_HTML = "html";
	private static final String EXTESION_PDF = "pdf";
	private String SEPARATOR = File.separator;
	private static final int RELATORIO_PDF = 1;
	private static final int RELATORIO_EXCEL = 2;
	private static final int RELATORIO_HTML = 3;
	private static final int RELATORIO_PLANILHA_OPEN_OFFICE = 4;
	private static final String PONTO = ".";
	private StreamedContent arquivoRetorno = null;
	private String caminhoArquivoRelatorio = null;
	private JRExporter tipoArquivoExportado = null;
	private String extansaoArquivoExportado = "";
	private String caminhoSubreport_dir = "";
	private File arquivoGerado = null;

	public StreamedContent geraRelatorio(List<?> listDataBeanColletionReport, HashMap parametrosRelatorio,
			String nomeRelatorioJasper, String nomeRelatorioSaida, int tipoRelatorio) throws Exception {

		/*
		 * Cria a lista de collectionDataSource de beans que carregam os dados para o
		 * relat�rio
		 */
		JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDataBeanColletionReport);

		/*
		 * Fornece o caminho fisico at� a pasta de cont�m os relat�rios compilados
		 * .jasper
		 */
		FacesContext context = FacesContext.getCurrentInstance();
		context.responseComplete();
		ServletContext scontext = (ServletContext) context.getExternalContext().getContext();

		String caminhoRelatorio = scontext.getRealPath(FOLDER_RELATORIOS);

		// EX: -> c:/aplica��o/relatorios/rel_bairo.jasper
		File file = new File(caminhoRelatorio + SEPARATOR + nomeRelatorioJasper + PONTO + "jasper");

		if (caminhoRelatorio == null 
				|| (caminhoRelatorio != null && caminhoRelatorio.isEmpty())
				|| !file.exists()) {
			caminhoRelatorio = this.getClass().getResource(FOLDER_RELATORIOS).getPath();
			SEPARATOR = "";
		}

		/* caminho para imagens */
		parametrosRelatorio.put("REPORT_PARAMETERS_IMG", caminhoRelatorio);

		/* caminho completo at� o relat�rio compilado indicado */
		String caminhoArquivoJasper = caminhoRelatorio + SEPARATOR + nomeRelatorioJasper + PONTO + "jasper";

		/* Faz o carregamento do relatorio indicado */
		JasperReport relatorioJasper = (JasperReport) JRLoader.loadObjectFromFile(caminhoArquivoJasper);

		/* Seta parametro SUBREPORT_DIR como caminho fisico para sub-reports */

		caminhoSubreport_dir = caminhoRelatorio + SEPARATOR;
		parametrosRelatorio.put(SUBREPORT_DIR, caminhoSubreport_dir);

		/* Carrega o arquivo compilado para a mem�ria */
		JasperPrint impressoraJasper = JasperFillManager.
				fillReport(relatorioJasper, parametrosRelatorio, jrbcds);

		switch (tipoRelatorio) {
		case RELATORIO_PDF:
			tipoArquivoExportado = new JRPdfExporter();
			extansaoArquivoExportado = EXTESION_PDF;
			break;
		
	/*
		case RELATORIO_HTML:
			tipoArquivoExportado = new JRHtmlExporter();
			extansaoArquivoExportado = EXTESION_HTML;
			break;
	*/
		case RELATORIO_EXCEL:
			tipoArquivoExportado = new JRXlsExporter();
			extansaoArquivoExportado = EXTESION_XLS;
			break;

		case RELATORIO_PLANILHA_OPEN_OFFICE:
			tipoArquivoExportado = new JROdtExporter();
			extansaoArquivoExportado = EXTESION_ODS;
			break;

		default:
			tipoArquivoExportado = new JRPdfExporter();
			extansaoArquivoExportado = EXTESION_PDF;
			break;
		}

		nomeRelatorioSaida += UNDERLINE + DataUtil.getDateAtualReportName();

		/* Caminho relatorio exportado */
		caminhoArquivoRelatorio = caminhoRelatorio + SEPARATOR + nomeRelatorioSaida + PONTO + extansaoArquivoExportado;

		/* Cria novo file exportado */
		arquivoGerado = new File(caminhoArquivoRelatorio);

		/* Preparar a impress�o */
		tipoArquivoExportado.setParameter(JRExporterParameter.JASPER_PRINT, impressoraJasper);

		/* Nome do arquivo fisico a ser impresso/exportado */
		tipoArquivoExportado.setParameter(JRExporterParameter.OUTPUT_FILE, arquivoGerado);

		/* executa a exporta��o */
		tipoArquivoExportado.exportReport();

		/* Remove o arquivo do servidor ap�s ser feito o download pelo usu�rio */
		arquivoGerado.deleteOnExit();

		/* Cria o inputstrwam para ser usado pelo PrimeFaces */
		InputStream conteudoRelatorio = new FileInputStream(arquivoGerado);

		/* Faz o retorno para a aplica��o */

		arquivoRetorno = new DefaultStreamedContent(conteudoRelatorio, "application/" + extansaoArquivoExportado,
				nomeRelatorioSaida + PONTO + extansaoArquivoExportado);

		return arquivoRetorno;

	}

}
