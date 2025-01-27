/*******************************************************************************
 * Copyright (c) 2020 Jonas Jordan
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/
package sernet.verinice.bp.rcp.filter;

import java.util.Set;

import sernet.verinice.model.common.CnATreeElement;

/**
 * Filters by dynamic (SNCA) enum property values.
 * 
 * @param <T>
 *            Enum class type.
 */
public class DynamicEnumPropertyFilter<T extends Enum<T>> extends AbstractDynamicPropertyFilter {

    private Set<T> filterValues;
    private Class<T> enumType;

    public DynamicEnumPropertyFilter(String dynamicPropertyType, Class<T> enumType,
            Set<T> filterValues) {
        super(dynamicPropertyType);
        this.filterValues = filterValues;
        this.enumType = enumType;
    }

    @Override
    protected boolean matches(CnATreeElement treeElement, String dynamicPropertyType) {
        return filterValues
                .contains(treeElement.getDynamicEnumProperty(dynamicPropertyType, enumType));
    }
}
