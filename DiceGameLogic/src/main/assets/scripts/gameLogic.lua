-- Arguments are the activity and the view.
local sysUtilsWrapper, gl3DScene = ...

local ROLLING_DICE_SOUND = 'rolling_dice.mp3'
local SKY_BOX_CUBE_MAP_OBJECT = 'SKY_BOX_CUBE_MAP_OBJECT';
local TERRAIN_MESH_OBJECT = 'TERRAIN_MESH_OBJECT';
local DICE_MESH_OBJECT = 'DICE_MESH_OBJECT_1';
local javaDiceObjName = luajava.newInstance('java.lang.String', DICE_MESH_OBJECT) --??? factory

onRollingObjectStart = function(gameObject)
    --if gameObject:getItemName():equals(DICE_MESH_OBJECT) then eq()???
        sysUtilsWrapper:iPlaySound(ROLLING_DICE_SOUND)
    --end
end

onRollingObjectStop = function(gameObject)
    sysUtilsWrapper:iStopSound()
end

beforeDrawFrame = function(frametime)
    --local objectType = luajava.bindClass('com.sadgames.gl3dengine.glrender.GLRenderConsts$GLObjectType')

    local skyBox = gl3DScene:getObject(SKY_BOX_CUBE_MAP_OBJECT)
    local terrainProgram = gl3DScene:getObject(TERRAIN_MESH_OBJECT):getProgram()

    skyBox:calcRotationAngle(frametime)
    terrainProgram:setSkyBoxRotationAngle(-skyBox:getRotationAngle())
end

