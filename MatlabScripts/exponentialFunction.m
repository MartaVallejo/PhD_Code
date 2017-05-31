function exponentialFunction()
%% Return the exponential function of the non urban prices data
% Load Data
cd('../General');
E = load('NonUrbanPrices.txt');
X=max(E(:,2:end),[],2);
y = [1:1:600];

ft=fittype('exp1');
cf=fit(y',X,ft)
cd('../Functions');
end

