package at.ac.tuwien.ac.heuoptws15.assignments.kpmpsolver.utils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class KPMPSolutionWriter {
	public static class PageEntry implements Cloneable{
		public int a, b;
		public int page;
		
		public PageEntry(int a, int b, int page) {
			this.a = a;
			this.b = b;
			this.page = page;
		}

		/*public Object clone() throws CloneNotSupportedException{
			PageEntry entry = (PageEntry)super.clone();
			entry.a = a;
			entry.b = b;
			entry.page = page;
			return entry;
		}*/

		@Override
		public PageEntry clone() {
			try {
				PageEntry entry = (PageEntry) super.clone();
				entry.a = a;
				entry.b = b;
				entry.page = page;
				return entry;
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return null;
		}


		@Override
		public int hashCode() {
			//return super.hashCode();
			return this.a+this.b+this.page;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PageEntry){
				PageEntry entry = (PageEntry)obj;
				if (entry.a == this.a && entry.b == this.b && entry.page == this.page){
					return true;
				}
				return false;
			}
			return false;
		}

		@Override
		public String toString() {
			return "PageEntry{" +
					"a=" + a +
					", b=" + b +
					", page=" + page +
					'}';
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
