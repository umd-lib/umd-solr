package edu.umd.lib;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexableField;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.schema.TrieDateField;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class UmdDateField extends TrieDateField {
	private SimpleDateFormat sdf;

	private String year = "yyyy";
	private String yearMonth = "yyyy-MM";
	private String yearMonthDay = "yyyy-MM-dd";
	// private static String PARAM_FROM_FORMAT = "fromFormat";
	private DateTimeFormatter yfmt = DateTimeFormat.forPattern(year);
	private DateTimeFormatter ymfmt = DateTimeFormat.forPattern(yearMonth);
	private DateTimeFormatter ymdfmt = DateTimeFormat.forPattern(yearMonthDay);

	@Override
	public boolean isPolyField() {
		return true;
	}

	@Override
	protected void init(IndexSchema schema, Map<String, String> args) {
		// if (args != null) {
		// fromFormat = args.remove(PARAM_FROM_FORMAT);
		// }
		// fmt = DateTimeFormat.forPattern(fromFormat);
		super.init(schema, args);
	}

	@Override
	public IndexableField createField(SchemaField field, Object value,
			float boost) {

		try {
			value = ymdfmt.parseDateTime(value.toString());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return super.createField(field, value, boost);
	}

	@Override
	public List<IndexableField> createFields(SchemaField field, Object value,
			float boost) {

		List<IndexableField> f = new ArrayList<IndexableField>();

		try {
			value = yfmt.parseDateTime(value.toString()).toDate();
			f.add( super.createField(field, value, boost));
		} catch (IllegalArgumentException ex) {
			try {
				value = ymfmt.parseDateTime(value.toString()).toDate();
				f.add( super.createField(field, value, boost));
			} catch (IllegalArgumentException ex1) {
				try {
					value = ymdfmt.parseDateTime(value.toString()).toDate();
					f.add( super.createField(field, value, boost));
				} catch (IllegalArgumentException ex2) {
					if(value instanceof String ) {
						String [] ar = ((String)value).split("-");
						if(ar.length == 2 && ar[0].length() == 4 && ar[1].length() == 4) {
							try {
								DateTime startTime = yfmt.parseDateTime(ar[0]);
								DateTime endTime = yfmt.parseDateTime(ar[1]);
								DateTime tmp = null;
								if(endTime.isBefore(startTime)) {
									tmp = startTime;
									startTime = endTime;
									endTime = tmp;
								}
								
								f.add( super.createField(field, endTime.toDate(), boost));
								tmp = startTime;
								while(tmp.isBefore(endTime)) {
									f.add( super.createField(field, tmp.toDate(), boost));
									tmp = tmp.plusYears(1);
								}
								
							} catch (IllegalArgumentException ex3) {
								
							}
						} 
					} else {
						throw ex2;
					}
				}
			}
		}

		return f;
	}

}
