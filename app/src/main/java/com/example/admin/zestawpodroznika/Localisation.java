package com.example.admin.zestawpodroznika;

public class Localisation
{
    private String Name;
    private double Longi;
    private double Lati;

    public Localisation(String name, double longi, double lati)
    {
        Name = name;
        Longi = longi;
        Lati = lati;
    }

    public Localisation()
    {
    }

    public double getLati()
    {
        return Lati;
    }

    public void setLati(double lati)
    {
        Lati = lati;
    }

    public double getLongi()
    {
        return Longi;
    }

    public void setLongi(double longi)
    {
        Longi = longi;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }
}
