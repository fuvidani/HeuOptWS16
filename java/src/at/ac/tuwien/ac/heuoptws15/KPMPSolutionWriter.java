package at.ac.tuwien.ac.heuoptws15;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public class KPMPSolutionWriter {
	private class PageEntry {
		public int a, b;
		public int page;
		
		public PageEntry(int a, int b, int page) {
			this.a = a;
			this.b = b;
			this.page = page;
		}
	}
	
	private int K = 0;
	private List<Integer> spineOrder = new LinkedList<>();
	private List<PageEntry> edgePartition = new LinkedList<>();
	
	public KPMPSolutionWriter(int K) {
		this.K = K;
	}
	
	public void setSpineOrder(List<Integer> spineOrder) {
		this.spineOrder = spineOrder;
	}
	
	public void addEdgeOnPage(int vertexA, int vertexB, int page) {
		edgePartition.add(new PageEntry(Math.min(vertexA, vertexB), Math.max(vertexA, vertexB), page));
	}
	
	public void write(String path) throws IOException {
		try(Writer w = new BufferedWriter(new FileWriter(path))) {
			write(w);
		}
	}
	
	public void write(Writer w) throws IOException {
		w.write(Integer.toString(spineOrder.size()));
		w.write('\n');
		w.write(Integer.toString(K));
		w.write('\n');
		
		for(int i: spineOrder) {
			w.write(Integer.toString(i));
			w.write('\n');
		}
		
		for(PageEntry e: edgePartition) {
			w.write(Integer.toString(e.a));
			w.write(' ');
			w.write(Integer.toString(e.b));
			w.write(" [");
			w.write(Integer.toString(e.page));
			w.write("]\n");
		}
	}
	
	public void print() {
		
		try(Writer w = new BufferedWriter(new OutputStreamWriter(System.out))) {
			write(w);
		} catch(IOException e) { e.printStackTrace(); }
	}
}
