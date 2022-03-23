/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

SELECT 'missing column' as remark, concat(c.table_name, '_history') as table_name, c.column_name as column_name, c.data_type as data_type FROM information_schema."columns" c
LEFT OUTER JOIN information_schema."columns" c_hist
ON concat(c.table_name, '_history') = c_hist.table_name AND c.column_name = c_hist.column_name
WHERE c.table_schema = 'public' AND c.table_name NOT LIKE '%_history' AND c.table_name NOT IN ('schema_version', 'systemevent') AND c.table_name NOT like 'pg_%'
AND c_hist.column_name IS NULL
/* exclude tables where the history table is missing altogether */
AND c.table_name NOT IN
  (SELECT t.table_name FROM information_schema."tables" t
   WHERE t.table_schema = 'public' AND t.table_name NOT LIKE '%_history'
   AND (SELECT COUNT(t_hist.table_name) FROM information_schema."tables" t_hist WHERE concat(t.table_name,'_history') = t_hist .table_name) = 0)
UNION
SELECT 'no history table' as remark, t.table_name, null as column_name, null as data_type FROM information_schema."tables" t
WHERE t.table_schema = 'public' AND t.table_name NOT LIKE '%_history' AND t.table_name NOT IN ('schema_version', 'systemevent') AND t.table_name NOT like 'pg_%'
AND (SELECT COUNT(t_hist.table_name) FROM information_schema."tables" t_hist WHERE concat(t.table_name,'_history') = t_hist .table_name) = 0
UNION
SELECT 'missing delete history trigger' as remark, t.table_name, null as column_name, null as data_type FROM information_schema."tables" t
WHERE t.table_schema = 'public'
  AND t.table_name NOT LIKE '%_history'
  AND t.table_name NOT LIKE '%_%'
  AND t.table_name NOT IN ('schema_version', 'systemevent')
  AND t.table_name NOT like 'pg_%'
  AND NOT exists(SELECT trigger_name
                 FROM information_schema.triggers
                 WHERE trigger_name = 'delete_history_trigger'
                   AND event_object_table = t.table_name)
UNION
SELECT 'missing versioning trigger' as remark, t.table_name, null as column_name, 'INSERT' as data_type FROM information_schema."tables" t
WHERE t.table_schema = 'public'
  AND t.table_name NOT LIKE '%_history'
  AND t.table_name NOT IN ('schema_version', 'systemevent')
  AND t.table_name NOT like 'pg_%'
  AND NOT exists(SELECT trigger_name
                 FROM information_schema.triggers
                 WHERE trigger_name = 'versioning_trigger'
                   AND event_manipulation = 'INSERT'
                   AND event_object_table = t.table_name)
UNION
SELECT 'missing versioning trigger' as remark, t.table_name, null as column_name, 'UPDATE' as data_type FROM information_schema."tables" t
WHERE t.table_schema = 'public'
  AND t.table_name NOT LIKE '%_history'
  AND t.table_name NOT IN ('schema_version', 'systemevent')
  AND t.table_name NOT like 'pg_%'
  AND NOT exists(SELECT trigger_name
                 FROM information_schema.triggers
                 WHERE trigger_name = 'versioning_trigger'
                   AND event_manipulation = 'UPDATE'
                   AND event_object_table = t.table_name)
UNION
SELECT 'missing versioning trigger' as remark, t.table_name, null as column_name, 'DELETE' as data_type FROM information_schema."tables" t
WHERE t.table_schema = 'public'
  AND t.table_name ~* '_'
  AND t.table_name NOT LIKE '%_history'
  AND t.table_name NOT IN ('schema_version', 'systemevent')
  AND t.table_name NOT like 'pg_%'
  AND NOT exists(SELECT trigger_name
                 FROM information_schema.triggers
                 WHERE trigger_name = 'versioning_trigger'
                   AND (event_manipulation = 'DELETE')
                   AND event_object_table = t.table_name)
UNION
SELECT 'invalid versioning trigger' as remark, t.table_name, null as column_name, 'DELETE' as data_type FROM information_schema."tables" t
WHERE t.table_schema = 'public'
  AND NOT (t.table_name ~* '_')
  AND t.table_name NOT LIKE '%_history'
  AND t.table_name NOT IN ('schema_version', 'systemevent')
  AND t.table_name NOT like 'pg_%'
  AND exists(SELECT trigger_name
                 FROM information_schema.triggers
                 WHERE trigger_name = 'versioning_trigger'
                   AND (event_manipulation = 'DELETE')
                   AND event_object_table = t.table_name)
ORDER BY remark, table_name , column_name;

