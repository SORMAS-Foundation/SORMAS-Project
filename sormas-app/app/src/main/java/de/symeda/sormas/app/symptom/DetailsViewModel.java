package de.symeda.sormas.app.symptom;

/**
 * Created by Orson on 19/04/2018.
 * <p>
 * www.technologyboard.org
 * sampson.orson@gmail.com
 * sampson.orson@technologyboard.org
 */
public class DetailsViewModel implements ISymptomViewModel {

    private String mDetail;

    public DetailsViewModel() {
        this.mDetail = "";
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        this.mDetail = detail;
    }
}
