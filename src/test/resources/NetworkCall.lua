--
-- Created by IntelliJ IDEA.
-- User: dcapwell
-- Date: 7/28/12
-- Time: 1:06 AM
-- To change this template use File | Settings | File Templates.
--

google = remoteGet("http://google.com")
yahoo = remoteGet("http://google.com")

-- Lua and LuaJ don't come with built in http support
-- This method is a wrapper around URL.openStream
function httpget(source)
    url = luajava.newInstance( "java.net.URL", source )
    sb = luajava.newInstance( "java.lang.StringBuilder" )

    reader = luajava.newInstance("java.io.BufferedReader", luajava.newInstance("java.io.InputStreamReader", url:openStream()))
    str = reader:readLine()
    while (str) do
        sb:append(str):append("\n")
        str = reader:readLine()
    end
    closeables = luajava.bindClass("com.google.common.io.Closeables")
    closeables.closeQuietly(reader)

    return sb:toString()
end

function awsmetadata(key)
    return httpget("http://169.254.169.254/latest/meta-data/" .. key)
end



aws = {
    availabilityzone = awsmetadata("placement/availability-zone")
}


print(aws.availabilityzone)