package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private List<Adiacenza> archi;
	private List<String> best;
	
	public Model() {
		dao = new EventsDao();
		archi=new ArrayList<Adiacenza>();
		
	}
	
	public List<Event> getEventi(){
		return this.dao.listAllEvents();
	}
	
	public List<Adiacenza> creaGrafo(String categoria, int mese) {
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiunta vertici
		Graphs.addAllVertices(this.grafo, dao.getVertici(categoria, mese));
		//aggiunta archi
		archi.addAll(this.dao.getArchi(categoria, mese));
		for(Adiacenza a: archi) {
			if(a.getPeso()>0) {
			Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}
		return archi;
		/*System.out.println("Grafo creato!");
		System.out.println("#VERTICI: "+this.grafo.vertexSet().size());
		System.out.println("#ARCHI: "+this.grafo.edgeSet().size());*/
			
	}
	public List<Adiacenza> getRisultato(){
		List<Adiacenza> risultato = new ArrayList<Adiacenza>();
		double media = calcolaMedia();
		System.out.println("PESO MEDIO: "+media);
		for(Adiacenza a: archi) {
			if(a.getPeso()>media)
				risultato.add(a);
		}
		return risultato;
		//oppure
		/*for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e)>media)
				risultato.add(new Adiacenza(this.grafo.getEdgeSource(e),this.grafo.getEdgeTarget(e),(int)this.grafo.getEdgeWeight(e)));
		}*/
	}

	private double calcolaMedia() {
		double media=0;
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			media+=this.grafo.getEdgeWeight(e);
		}
		media= media/this.grafo.edgeSet().size();
		return media;
	}
	
	
	public List<String> calcolaPercorso(String sorgente, String destinazione){
		best = new LinkedList<String>();
		List<String> parziale = new LinkedList<String>();
		parziale.add(sorgente);
		cerca(parziale, destinazione);
		return best;
	}
	
	private void cerca(List<String> parziale,String destinazione) {
		//condizione di terminazione
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			//è la sol migliore?
			if(parziale.size()>best.size())
				best=new LinkedList<String>(parziale);
			return;
		}
		//scorro i vicini dell'ultimo inserito e provo le varie strade
		for(String v: Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(v)) {
				parziale.add(v);
				cerca(parziale,destinazione);
				parziale.remove(parziale.size()-1);
			}
			
		}
	}
	
}
