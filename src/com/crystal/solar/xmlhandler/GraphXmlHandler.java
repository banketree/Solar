package com.crystal.solar.xmlhandler;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.crystal.solar.bean.Graph;
import com.crystal.solar.bean.Graphs;
import com.crystal.solar.bean.Error;

public class GraphXmlHandler extends DefaultHandler
{

	private static final String GRAPHS = "graphs";
	private static final String GRAPH = "graph";
	private static final String ERROR = "error";

	private String tagName;

	private Graphs graphs;
	private Graph graph;
	private Error error;

	private boolean isError;

	private StringBuffer sb = new StringBuffer();

	@Override
	public void startDocument() throws SAXException
	{
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{

		if (GRAPHS.equals(localName))
		{
			graphs = new Graphs();
			graphs.graphList = new ArrayList<Graph>();
		} else if (GRAPH.equals(localName))
		{
			graph = new Graph();
		} else if (ERROR.equals(localName))
		{
			error = new Error();
			isError = true;
		}
		this.tagName = localName;

		sb.setLength(0);

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		sb.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (tagName != null)
		{
			if (tagName.equals("status"))
			{
				if (isError)
				{
					error.status = sb.toString();
				}
			} else if (tagName.equals("errorCode"))
			{
				if (isError)
				{
					error.errorCode = sb.toString();
				}
			} else if (tagName.equals("errorMessage"))
			{
				if (isError)
				{
					error.errorMessage = sb.toString();
				}
			} else if (tagName.equals("daypower") || tagName.equals("monthpower") || tagName.equals("yearpower") || tagName.equals("overall"))
			{
				graphs.datepower = sb.toString();
			} else if (tagName.equals("income"))
			{
				graphs.income = sb.toString();
			} else if (tagName.equals("savetree"))
			{
				graphs.savetree = sb.toString();
			} else if (tagName.equals("saveco2"))
			{
				graphs.saveco2 = sb.toString();
			} else if (tagName.equals("datetime"))
			{
				graph.datetime = sb.toString();
			} else if (tagName.equals("power"))
			{
				graph.power = sb.toString();
			}
		}

		if (GRAPH.equals(localName))
		{
			graphs.graphList.add(graph);
			graph = null;
		}

		this.tagName = null;
	}

	public Object getResult()
	{
		return graphs == null ? error : graphs;
	}
}
