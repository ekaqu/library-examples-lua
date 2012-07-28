--
-- Created by IntelliJ IDEA.
-- User: dcapwell
-- Date: 7/28/12
-- Time: 12:01 AM
-- To change this template use File | Settings | File Templates.
--

foo = "bar"

function cat(t)
    return function(table)
        for k, v in pairs(t) do
            table[k] = table[k] or v
        end
        return table
    end
end