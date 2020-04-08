package br.com.project.listener;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@ApplicationScoped //INSTANCIA UNICA PARA O PROJETO INTEIRO
public class ContextLoaderListenerCaixakiUtils extends ContextLoader implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 
	 * Retorna todo o contexto do spring (ambiente de execu��o)
	 * @return
	 */
	private static WebApplicationContext getWac() {
		return WebApplicationContextUtils.getWebApplicationContext(getCurrentWebApplicationContext().getServletContext());
	}
	
	public static Object getBean(String idNomeBean) {
		return getWac().getBean(idNomeBean);
		
	}
	
	public static Object getBean(Class<?> classe) {
		return getWac().getBean(classe);
		
	}

}