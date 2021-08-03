/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

/*
 This script can be executed on any database and lists all differences regarding column-names or column-datatypes between each table and it's _history equivalent.
 */

SELECT table_name_join.j_original_table AS original_table,
       table_name_join.j_history_table AS history_table,
       allcolumns.column_name AS differing_column,
       allcolumns.data_type AS datatype
FROM INFORMATION_SCHEMA.COLUMNS AS allcolumns
LEFT JOIN (
    SELECT table_name AS j_history_table, substring(table_name, 1, length(table_name)-8) AS j_original_table
    FROM INFORMATION_SCHEMA.TABLES
    WHERE table_schema='public'
      AND table_name LIKE '%\_history'
) AS table_name_join
ON allcolumns.table_name = table_name_join.j_history_table OR allcolumns.table_name = table_name_join.j_original_table
WHERE allcolumns.table_name=j_original_table
   OR allcolumns.table_name=j_history_table
GROUP BY differing_column, datatype, original_table, history_table
HAVING COUNT(*)!=2
ORDER BY original_table, differing_column