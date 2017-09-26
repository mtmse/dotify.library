/*
 * Braille Utils (C) 2010-2011 Daisy Consortium 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com_indexbraille;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.daisy.braille.impl.spi.SPIHelper;
import org.daisy.braille.utils.api.embosser.Embosser;
import org.daisy.braille.utils.api.embosser.EmbosserProvider;
import org.daisy.braille.utils.api.factory.FactoryProperties;
import org.daisy.braille.utils.api.table.TableCatalogService;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class IndexEmbosserProvider implements EmbosserProvider {
	public static enum EmbosserType implements FactoryProperties {
//		INDEX_3_7("", ""), //Not implemented
//		INDEX_ADVANCED("", ""), //Not implemented
//		INDEX_CLASSIC("", ""), //Not implemented
//		INDEX_DOMINO("", ""), //Not implemented
//		INDEX_EVEREST_S_V1("", ""), //Not implemented
//		INDEX_EVEREST_D_V1("", ""), //Not implemented
		INDEX_BASIC_BLUE_BAR("Index Basic Blue-Bar", "Early Index Basic embosser", (t, u)->new BlueBarEmbosser(t, u)),
		INDEX_BASIC_S_V2("Index Basic-S V2", "", (t, u)->new IndexV2Embosser(t, u)),
		INDEX_BASIC_D_V2("Index Basic-D V2", "", (t, u)->new IndexV2Embosser(t, u)),
		INDEX_EVEREST_D_V2("Index Everest-D V2", "", (t, u)->new IndexV2Embosser(t, u)),
		INDEX_4X4_PRO_V2("Index 4X4 Pro V2", "", (t, u)->new IndexV2Embosser(t, u)),
		INDEX_BASIC_S_V3("Index Basic-S V3", "", (t, u)->new IndexV3Embosser(t, u)),
		INDEX_BASIC_D_V3("Index Basic-D V3", "", (t, u)->new IndexV3Embosser(t, u)),
		INDEX_EVEREST_D_V3("Index Everest-D V3", "", (t, u)->new IndexV3Embosser(t, u)),
		INDEX_4X4_PRO_V3("Index 4X4 Pro V3", "", (t, u)->new IndexV3Embosser(t, u)),
		INDEX_4WAVES_PRO_V3("Index 4Waves Pro", "", (t, u)->new IndexV3Embosser(t, u)),
		INDEX_BASIC_D_V4("Index Basic-D V4", "", (t, u)->new IndexV4Embosser(t, u)),
		INDEX_EVEREST_D_V4("Index Everest-D V4", "", (t, u)->new IndexV4Embosser(t, u)),
		INDEX_BRAILLE_BOX_V4("Index Braille Box V4", "", (t, u)->new IndexV4Embosser(t, u)),
		INDEX_BASIC_D_V5("Index Basic-D V5", "", (t, u)->new IndexV4Embosser(t, u)),
		INDEX_EVEREST_D_V5("Index Everest-D V5", "", (t, u)->new IndexV4Embosser(t, u)),
		INDEX_BRAILLE_BOX_V5("Index Braille Box V5", "", (t, u)->new IndexV4Embosser(t, u)),
		INDEX_FANFOLD_V5("Index Fanfold", "", (t, u)->new IndexV4Embosser(t, u));

		private final String name;
		private final String desc;
		private final String identifier;
		private final BiFunction<TableCatalogService, EmbosserType, Embosser> instanceCreator;
		EmbosserType (String name, String desc, BiFunction<TableCatalogService, EmbosserType, Embosser> instanceCreator) {
			this.name = name;
			this.desc = desc;
			this.identifier = this.getClass().getCanonicalName() + "." + this.toString();
			this.instanceCreator = instanceCreator;
		}
		@Override
		public String getIdentifier() {
			return identifier;
		}
		@Override
		public String getDisplayName() {
			return name;
		}
		@Override
		public String getDescription() {
			return desc;
		}
		
		private Embosser newInstance(TableCatalogService tableCatalogService) {
			return instanceCreator.apply(tableCatalogService, this);
		}
	};

	private final Map<String, FactoryProperties> embossers;
	private TableCatalogService tableCatalogService = null;

	public IndexEmbosserProvider() {
		embossers = new HashMap<String, FactoryProperties>();
		for (EmbosserType e : EmbosserType.values()) {
			embossers.put(e.getIdentifier(), e);			
		}
	}

	@Override
	public Embosser newFactory(String identifier) {
		FactoryProperties fp = embossers.get(identifier);
		return fp==null?null:((EmbosserType)fp).newInstance(tableCatalogService);
	}

	@Override
	public Collection<FactoryProperties> list() {
		return Collections.unmodifiableCollection(embossers.values());
	}

	@Reference
	public void setTableCatalog(TableCatalogService service) {
		this.tableCatalogService = service;
	}

	public void unsetTableCatalog(TableCatalogService service) {
		this.tableCatalogService = null;
	}

	@Override
	public void setCreatedWithSPI() {
		if (tableCatalogService==null) {
			tableCatalogService = SPIHelper.getTableCatalog();
		}
	}

}
