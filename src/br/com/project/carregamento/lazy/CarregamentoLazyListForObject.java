package br.com.project.carregamento.lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.framework.controller.crud.Controller;
import br.com.project.listener.ContextLoaderListenerCaixakiUtils;

/**
 * Classe respos�vel pelo carregamento sobre demanda 'LazyLoad'
 * @author deh0_
 *
 * @param <T>
 */
public class CarregamentoLazyListForObject<T> extends LazyDataModel<T> {

	private static final long serialVersionUID = 1L;
	
	//LISTA GEN�RICA DE DADOS
	private List<T> list = new ArrayList<>();
	
	private int totalRegistroConsulta = 0;
	
	private String query = null;
	
	private Controller controller = (Controller) ContextLoaderListenerCaixakiUtils.getBean(Controller.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<T> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, String> filters) {
		try {
			if (query != null && !query.isEmpty()) {
				//REALIZAR CAST EM TUDO QUE FOR GEN�RICO
				list = (List<T>) controller.findListByQueryDinamica(query,
						first, pageSize);
				if (totalRegistroConsulta == 0) {
					setRowCount(0);
				}else {
					setRowCount(totalRegistroConsulta);
				}
			}
			setPageSize(pageSize);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (List<T>) list;
	}
	
	public void setTotalRegistroConsulta(int totalRegistroConsulta, String queryDeBuscaConsulta) {
		this.query = queryDeBuscaConsulta;
		this.totalRegistroConsulta = totalRegistroConsulta;
	}
	
	public List<T> getList() {
		return list;
	}
	
	public void clean() {
		this.query = null;
		this.totalRegistroConsulta = 0;
		this.list.clear();
	}
	
	public void remove(T objetoSelecionado) {
		this.list.remove(objetoSelecionado);
	}
	
	public void add(T objetoSelecionado) {
		this.list.add(objetoSelecionado);
	}
	
	public void add(List<T> collections) {
		this.list.addAll(collections);
	}
	
}