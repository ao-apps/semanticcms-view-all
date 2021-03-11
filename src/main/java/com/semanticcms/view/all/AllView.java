/*
 * semanticcms-view-all - SemanticCMS view of all content of the current page and all child pages.
 * Copyright (C) 2016, 2017, 2020, 2021  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of semanticcms-view-all.
 *
 * semanticcms-view-all is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * semanticcms-view-all is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with semanticcms-view-all.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.semanticcms.view.all;

import com.aoindustries.html.FlowContent;
import com.aoindustries.html.servlet.DocumentEE;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.web.resources.registry.Registry;
import com.aoindustries.web.resources.registry.Style;
import com.aoindustries.web.resources.servlet.RegistryEE;
import com.semanticcms.core.model.Page;
import com.semanticcms.core.servlet.PageUtils;
import com.semanticcms.core.servlet.SemanticCMS;
import com.semanticcms.core.servlet.Theme;
import com.semanticcms.core.servlet.View;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.SkipPageException;

/**
 * The view of all content of the current page and all child pages.
 *
 * TODO: Authorship and copyright in view-all, or author specified per article, or author in article when doesn't match overall page?
 *
 * TODO: Any use of description/keywords per article below?
 */
public class AllView extends View {

	public static final String NAME = "all";

	public static final com.aoindustries.web.resources.registry.Group.Name RESOURCE_GROUP = new com.aoindustries.web.resources.registry.Group.Name("semanticcms-view-all");

	// TODO: Change to Group.Name once we have group-level ordering
	public static final Style SEMANTICCMS_VIEW_ALL_PRINT = Style.builder()
		.uri("/semanticcms-view-all/semanticcms-view-all-print.css")
		.media("print")
		.build();

	private static final String JSPX_TARGET = "/semanticcms-view-all/view.inc.jspx";

	@WebListener("Registers the \"" + NAME + "\" view in RegistryEE and SemanticCMS.")
	public static class Initializer implements ServletContextListener {
		@Override
		public void contextInitialized(ServletContextEvent event) {
			ServletContext servletContext = event.getServletContext();

			// Add our CSS file
			RegistryEE.Application.get(servletContext)
				.getGroup(RESOURCE_GROUP)
				.styles
				.add(SEMANTICCMS_VIEW_ALL_PRINT);

			// Add this view
			SemanticCMS.getInstance(servletContext).addView(new AllView());
		}
		@Override
		public void contextDestroyed(ServletContextEvent event) {
			// Do nothing
		}
	}

	private AllView() {}

	@Override
	public Group getGroup() {
		return Group.FIXED;
	}

	@Override
	public String getDisplay() {
		return "View All";
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isApplicable(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, Page page) throws ServletException, IOException {
		return PageUtils.hasChild(page);
	}

	@Override
	public String getDescription(Page page) {
		return null;
	}

	@Override
	public String getKeywords(Page page) {
		return null;
	}

	/**
	 * No search index "all" view to avoid duplicate content penalties.
	 */
	@Override
	public boolean getAllowRobots(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, Page page) {
		return false;
	}

	@Override
	public void configureResources(ServletContext servletContext, HttpServletRequest req, HttpServletResponse resp, Theme theme, Page page, Registry requestRegistry) {
		super.configureResources(servletContext, req, resp, theme, page, requestRegistry);
		requestRegistry.activate(RESOURCE_GROUP);
		// TODO: Add and activate all the page-scope registries from all pages that will be written
	}

	@Override
	public <__ extends FlowContent<DocumentEE, __>> void doView(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response, __ flow, Page page) throws ServletException, IOException, SkipPageException {
		Dispatcher.include(
			servletContext,
			JSPX_TARGET,
			request,
			response,
			Collections.singletonMap("page", page)
		);
	}
}
