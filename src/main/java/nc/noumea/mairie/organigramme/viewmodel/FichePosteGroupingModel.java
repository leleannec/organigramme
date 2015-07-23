package nc.noumea.mairie.organigramme.viewmodel;

import java.util.Comparator;
import java.util.List;

import nc.noumea.mairie.organigramme.dto.FichePosteDto;

import org.zkoss.zul.GroupsModelArray;

public class FichePosteGroupingModel extends GroupsModelArray<FichePosteDto, String, String, Object> {
	private static final long	serialVersionUID	= 1L;

	public FichePosteGroupingModel(List<FichePosteDto> data, Comparator<FichePosteDto> cmpr) {
		super(data.toArray(new FichePosteDto[0]), cmpr);
	}

	protected String createGroupHead(FichePosteDto[] groupdata, int index, int col) {
		if (groupdata.length > 0) {
			return groupdata[0].getSigle() + "(" + groupdata.length + ")";
		}

		return "";
	}
}
