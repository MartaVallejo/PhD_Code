function myFunctions2( n )
%% This file has information regarding all functions created in the system
% % ///////////////////////////////////////////////////////////////////////

switch n
    case 0
    %% Generate a file budget dependant on the population
    % Input: 
    %   alpha: measure the importance of the population when the budget
    %   is generated.
    %   Density.txt: File with population evolution
    alpha = 0.5;
    budgetGenerator(alpha);
    
    case 1
    %% Show simple satisfaction comparative for GA, MO, CLO and RAN
    matlab
    cd('../Functions');
    simpleSatisfaction
    
    case 2
    %% Show simple satisfaction comparative for GA, CLO and RAN
    matlab
    cd('../Functions');
    simpleSatisfactionBar
    
    case 3
    %% Show simple satisfaction comparative for GA and RAN
    matlab
    cd('../Functions');
    satisfactionTable50
    
    case 4
    %% Create the code in latex for a table with the satisfaction achieved
    %by the three approaches
    matlab2
    cd('../Functions');
    latexTable
    
    case 5
    %% Create a plot with the best Test values achieved
    % !!!!!!It doesn't work
    matlab
    cd('../Functions');
    bestSatisfactionTest
    
    case 6
    %% Create a stacked bar chart using the bar function
    % !!!!!!It doesn't work. Inconsistencies has change
    failuresPlot
    
    case 7
    %% Create a plot with the population behaviour
    matlab
    cd('../Functions');
    populationPlot
    
    case 8
    %% Create a plot with the number of cells urbanised
    matlab
    cd('../Functions');
    cellsUrbanisedPlot
    
    case 9
    %% Create a plot with the number of cells protected
    matlab
    cd('../Functions');
    protectedCellsPlot
    
    case 10
    %% Create a plot with the behaviour of the migration
    matlab
    cd('../Functions');
    migrationPlot
    
    case 11
    %% Create a plot with the collected green prices with the lowest value
    matlab
    cd('../Functions');
    lowerGreenPricesPlot
    
    case 12
    %% Create a plot with the collected green prices with the highest value
    matlab
    cd('../Functions');
    higherGreenPricesPlot
    
    case 13
    %% Test the 3-D shaded surface plot for collected green prices
    matlab
    cd('../Functions');
    greenSpacesPlot3D
    
    case 14
    %% Create a plot with the urban cells with the lowest value
    matlab
    cd('../Functions');
    lowestUrbanPrices
    
    case 15
    %% Create a plot with the urban cells with the highest value
    matlab
    cd('../Functions');
    higherUrbanPricesPlot
    
    case 16
    %% Create a plot with the average of green prices
    matlab
    cd('../Functions');
    avgGreenPricesPlot
    
    case 17
    %% Create a plot with the urban cells with the average value
    matlab
    cd('../Functions');
    avgUrbanPricesPlot
    
    case 18
    %% Create a plot with the GA data
    % !!!!!!It doesn't work. GA_Data file has change
    matlab
    cd('../Functions');
    GADataPlot
    
    case 19
    %% Plot average green price prices grouped by rings
    %Place Ring.txt in General folder before run the function
    greenPricesPerRing
    
    case 20
    %% Double Plot
    % 1.- Plot average green price prices grouped by rings
    %   In which tick of the clock?
    % 2.- Average green prices in GA
    matlab
    cd('../Functions');
    Ring_GreenPricesPlot
    
    case 21
    %% Return the exponential function of the non urban prices data
    matlab
    cd('../Functions');
    exponentialFunction

    case 22
    %% 3subplot: satisfaction, number of cells protected and closeness
    %ICCS plot
    matlab2
    cd('../Functions');
    satisfaction_protected_closeness
    
    case 23
    %% Create a plot with the area satisfaction
    % optimisation approaches
    matlab4
    cd('../Functions');
    satisfactionArea()
    
    case 24
    %% Plot with the number of cells protected for Online/Offline/Mix
    matlab3
    cd('../Functions');
    OnOfCellsProtectedPlot
    
    case 25
    %% Create a line plot with the satisfaction for Online/Offline/Mix
    matlab3
    cd('../Functions');
    OnOfSatisfaction
    
    case 26
    %% Create a plot with the closeness to CBD for Online/Offline/Mix
    matlab3
    cd('../Functions');
    OnOfCloseness
    
    case 27
    %% Computational time for Online/Offline/Mix
    % TODO. Not really implemented
    matlab3
    cd('../Functions');
    OnOfTiming
    
    case 28
    %% Plot the function with the average remaining Budget
    matlab2
    cd('../Functions');
    remainingBudget
    
    case 29
    %% Plot the function with the average remaining Budget
    GAOptimisationTable()
    
    case 30
    %% Remaining Budget for Online/Offline/Mix
    matlab3
    cd('../Functions');
    OnOfBudget
    
    case 31
    %% Plot the function with the average remaining Budget
    matricesCorr
    
    case 32
    %% 3subplot: satisfaction, number of cells protected and closeness
    %ICCS plot
    matlab2b
    cd('../Functions');
    satisfaction_protected_closeness
    
    case 33
    %% Create a line plot with the evolution for Online approach
    %JAIR plot
    matlab3
    cd('../Functions');
    OnOfEvolution
    
    %% Create a plot with the evolution of the population (young, mature, old)
    % totalPopulationPlot() - For thesis
    
    %% Create a double plot with the highest and lowest prices for urban cells
    % higherLowerUrbanPrices - For thesis
    
    %% Create a plot with the evolution of the salary
    % avgSalaryPlot - For thesis
    
end

