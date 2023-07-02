/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package DAOs;

/**
 *
 * @author GIGABYTE
 */
import java.util.ArrayList;

public interface DAOinterface<T> {
	public int insert(T t);

	public int update(T t);

	public int delete(T t);

	public ArrayList<T> selectAll();

	public T selectById(String st);

	public ArrayList<T> selectByCondition(String condition);
}

