package me.oreos.iam.types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import me.oreos.iam.entities.enums.EffectiveScopeEnum;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import org.hibernate.HibernateException;

public class PostgresEffectiveScopeEnum implements UserType {

    public PostgresEffectiveScopeEnum() {
        // Default constructor required by Hibernate
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.OTHER}; // Use Types.OTHER for PostgreSQL ENUM
    }

    @Override
    public Class returnedClass() {
        return EffectiveScopeEnum.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        return value == null ? null : EffectiveScopeEnum.valueOf(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, ((EffectiveScopeEnum) value).name(), Types.OTHER);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value; // Enums are immutable
    }

    @Override
    public boolean isMutable() {
        return false; // Enums are immutable
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value; // Enums are serializable
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original; // Enums are immutable
    }
}