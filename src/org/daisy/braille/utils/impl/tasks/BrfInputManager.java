package org.daisy.braille.utils.impl.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.table.TableCatalogService;
import org.daisy.streamline.api.tasks.InternalTask;
import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskSystemException;

class BrfInputManager implements TaskGroup {
	private final TableCatalogService tableCatalog;

	BrfInputManager(TableCatalogService tableCatalog) {
		this.tableCatalog = tableCatalog;
	}

	@Override
	public List<InternalTask> compile(Map<String, Object> parameters) throws TaskSystemException {
		List<InternalTask> ret = new ArrayList<>();
		ret.add(new Brf2PefTask(Messages.TITLE_BRF_TO_PEF.localize(), parameters, tableCatalog));
		return ret;
	}

}