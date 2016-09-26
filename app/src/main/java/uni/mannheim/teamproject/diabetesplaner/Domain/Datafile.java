package uni.mannheim.teamproject.diabetesplaner.Domain;

import java.util.Date;

/**
 * Created by Stefan on 26.09.2016.
 */
public class Datafile {
    private Date date;
    private String title;

    public Datafile(String title, Date date){
        this.date = date;
        this.title = title;
    }

    public Date getDate(){
        return this.date;
    }

    public String getTitle(){
        return this.title;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public void setTitle(String title){
        this.title = title;
    }
}
