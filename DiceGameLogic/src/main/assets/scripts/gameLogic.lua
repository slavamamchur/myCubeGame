local sysUtilsWrapper, gl3DScene, restApi = ...

local ROLLING_DICE_SOUND = 'rolling_dice.mp3'
local SKY_BOX_CUBE_MAP_OBJECT = 'SKY_BOX_CUBE_MAP_OBJECT'
local TERRAIN_MESH_OBJECT = 'TERRAIN_MESH_OBJECT'
local CHIP_MESH_OBJECT = 'CHIP_MESH_OBJECT'
local DICE_MESH_OBJECT = 'DICE_MESH_OBJECT_1'

local CHIP_DEFAULT_WEIGHT = 1.0
local COLLISION_OBJECT = 1
local TERRAIN_MATERIAL = 1
local DEFAULT_TEXTURE_SIZE = 500
local GAME_STATE_WAIT = 0

local DICE_FACES_VALUES = {68, 85, 17, 0, 51, 34}

local ON_PLAY_TURN_ANIMATION_END = 'rollDice'
local ON_STOP_MOVING_ANIMATION_END = 'playerNextMove'

onRollingObjectStart = function(gameObject)
    if gameObject == gl3DScene:getObject(DICE_MESH_OBJECT) then
        sysUtilsWrapper:iPlaySound(ROLLING_DICE_SOUND)
    end
end

onRollingObjectStop = function(gameObject)
    sysUtilsWrapper:iStopSound()
end

onMovingObjectStop = function(gameObject, gameInstance)
    if not (gameInstance == nil) and (gameObject == gl3DScene:getObject(DICE_MESH_OBJECT)) then
        gameInstance:setStepsToGo(getTopFaceDiceValue(gameObject))
        restApi:showTurnInfo(gameInstance)

        gl3DScene:restorePrevViewMode()

        gameObject:hideObject()

        gl3DScene:setZoomCameraAnimation(gl3DScene:createZoomCameraAnimation(2.0))
        gl3DScene:getZoomCameraAnimation():startAnimation(nil, ON_STOP_MOVING_ANIMATION_END, {gameInstance})
    end
end

beforeDrawFrame = function(frametime)
    local skyBox = gl3DScene:getObject(SKY_BOX_CUBE_MAP_OBJECT)

    skyBox:calcRotationAngle(frametime)
    gl3DScene:getObject(TERRAIN_MESH_OBJECT):getProgram():setSkyBoxRotationAngle(-skyBox:getRotationAngle())
end

onPlayTurn = function()
    gl3DScene:setZoomCameraAnimation(gl3DScene:createZoomCameraAnimation(0.5))
    gl3DScene:getZoomCameraAnimation():startAnimation(nil, ON_PLAY_TURN_ANIMATION_END, {})
end

rollDice = function()
    local dice = gl3DScene:getObject(DICE_MESH_OBJECT)

    gl3DScene:switrchTo2DMode()

    dice:createRigidBody()
    dice:setPWorldTransform(generateDiceInitialTransform())
    dice:get_body():setLinearVelocity(generateForceVector())

    gl3DScene:getPhysicalWorldObject():addRigidBody(dice:get_body())
end

playerNextMove = function(gameInstance)
    restApi:moveGameInstance(gameInstance)
end

onDiceObjectInit = function(gameObject)
    gameObject:hideObject()
end



onGameRestarted = function(gameInstanceEntity)
    gameInstanceEntity:setStateLua(GAME_STATE_WAIT)
    gameInstanceEntity:setCurrentPlayer(0)
    gameInstanceEntity:setStepsToGo(0)

    for i = 0, gameInstanceEntity:getPlayers():size() - 1 do
        local player = gameInstanceEntity:getPlayers():get(i)
        player:setCurrentPoint(0)
        player:setFinished(false)
        player:setSkipped(false)
    end

    pcall(moveChips, gameInstanceEntity)
end

moveChips = function(gameInstanceEntity)
    local playersOnWayPoints = {}
    for i = 1, gameInstanceEntity:getGame():getGamePoints():size() do
        table.insert(playersOnWayPoints, i, 0)
    end

    for i = 0, gameInstanceEntity:getPlayers():size() - 1 do
        local player = gameInstanceEntity:getPlayers():get(i)
        local chip = gl3DScene:getObject(string.format('%s_%s', CHIP_MESH_OBJECT, player:getName()))
        local currentPointIdx = player:getCurrentPoint() + 1

        playersOnWayPoints[currentPointIdx] = playersOnWayPoints[currentPointIdx] + 1
        chip:setInWorldPosition(getChipPlace(gameInstanceEntity:getGame():getGamePoints():get(player:getCurrentPoint()),
                                  playersOnWayPoints[currentPointIdx] - 1,
                                     true))
    end
end

onCreateDynamicItems = function(gameEntity, gameInstance)
    if (gameInstance == nil) or (gameEntity:getGamePoints() == nil) then
        return
    end

    local prevChip = nil
    local playersOnWayPoints = {}

    for i = 1, gameEntity:getGamePoints():size() do
        table.insert(playersOnWayPoints, i, 0)
    end

    for i = 0, gameInstance:getPlayers():size() - 1 do
        local player = gameInstance:getPlayers():get(i)
        local chip = gameEntity:createNewItem(CHIP_MESH_OBJECT,
                                              TERRAIN_MESH_OBJECT,
                                              CHIP_DEFAULT_WEIGHT,
                                              COLLISION_OBJECT,
                                              TERRAIN_MATERIAL)
        :createSceneObject(sysUtilsWrapper, gl3DScene, player:getColor())

        chip:setInitialScale(0.2)
        chip:setInitialTranslation(0.0, 0.08, 0.0)
        chip:setTwoSidedSurface(false);
        chip:setItemName(string.format('%s_%s', CHIP_MESH_OBJECT, player:getName()))

        local parent = gl3DScene:getObject(TERRAIN_MESH_OBJECT)
        local currentPointIdx = player:getCurrentPoint() + 1
        playersOnWayPoints[currentPointIdx] = playersOnWayPoints[currentPointIdx] + 1
        chip:setInWorldPosition(getChipPlace(gameEntity:getGamePoints():get(player:getCurrentPoint()),
                                            playersOnWayPoints[currentPointIdx] - 1,
                                            true))

        if prevChip == nil then
            chip:loadObject()
        else
            chip:loadFromObject(prevChip)
        end

        parent:putChild(chip, chip:getItemName())
        prevChip = chip
    end
end

function getChipPlace(point, playersCnt, rotate)
    local map = gl3DScene:getObject(TERRAIN_MESH_OBJECT)
    local scaleFactor = map:getGlTexture():getWidth() * 1.0 / DEFAULT_TEXTURE_SIZE
    local toX2 = point:getxPos() * scaleFactor
    local toZ2 = point:getyPos() * scaleFactor

    if rotate then
        local angle = getChipRotationAngle(playersCnt)
        toX2 = toX2 - 7.5 * scaleFactor * math.sin(angle)
        toZ2 = toZ2 - 7.5 * scaleFactor * math.cos(angle)
    end

    return map:map2WorldCoord(toX2, toZ2)
end

function getChipRotationAngle(playersCnt)
    if playersCnt == 0 then
        return 0.0
    end

    local part = 8
    local b
    local angle

    repeat
        angle = 360.0 / part
        b = part - 1
        part = part / 2
    until not ((math.fmod(playersCnt, part) == 0) and not (part == 1))

    return math.rad((2 * playersCnt - b) * angle)
end

function getTopFaceDiceValue(dice)
    local result = 0
    local max_y = 0.0
    local normals = dice:getRaw3DModel():getNormalsLua()

    for i = 1, #normals / 3 do
        local normalVector = sysUtilsWrapper:mulMV(dice:getModelMatrix(), {normals[i * 3 - 2], normals[i * 3 - 1], normals[i * 3], 1.0})

        if normalVector.y > max_y then
            max_y = normalVector.y;
            result = i - 1;
        end
    end

    local key = getKeyForValue(DICE_FACES_VALUES, result)
    if key == nil then
        key = 0
    end

    return key
end

function getKeyForValue(tbl, value)
    local result = nil

    for k, v in pairs(tbl) do
        if v == value then
            result = k
            break
        end
    end

    return result
end

function generateForceVector()
    math.randomseed(os.time())

    local fxz = 3.5 + math.random(0, 1) * 1.0 --2
    local fy = fxz * 3.0 / 4.0
    local fVector = {0.0, fy, -fxz, 1.0}

    local transform = sysUtilsWrapper:createTransform()
    transform:rotY(math.rad(45.0 - math.random(0, 90) * 1.0))

    return sysUtilsWrapper:mulMV(transform, fVector)
end

function generateDiceInitialTransform()
    math.randomseed(os.time())

    local transformer = sysUtilsWrapper:createTransform()
    local transformingObject = sysUtilsWrapper:createTransform()

    transformingObject:setIdentity();
    transformer:setIdentity();

    transformer:setTranslation(sysUtilsWrapper:createVector3f(0.0, 0.5, 2.5))
    transformingObject:mul(transformer)

    transformer:rotX(math.rad(math.random(0, 3) * 90.0)) --4
    transformingObject:mul(transformer)

    transformer:rotY(math.rad(math.random(0, 3) * 90.0))
    transformingObject:mul(transformer)

    transformer:rotZ(math.rad(math.random(0, 3) * 90.0))
    transformingObject:mul(transformer)

    return transformingObject
end

function make_array(dataClass, values)
    local arrayClass = luajava.bindClass("java.lang.reflect.Array")
    if(arrayClass == nil) then
        print("Can't get array class")
        return nil
    end

    local newTypedArray = arrayClass:newInstance(dataClass, #values)
    if(newTypedArray == nil) then
        print("Can't get array class")
        return nil
    end

    for i=1,#values do
        arrayClass:set(newTypedArray, i-1, values[i])
    end

    return newTypedArray
end
