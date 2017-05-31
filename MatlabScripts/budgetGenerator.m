function budgetGenerator(alpha)
% This function generate a file with the budget linked with the population

format shortG 
X = load('../Budget/Density.txt');
Y = sum(X(:,2:end),2);
Z = int32(Y*alpha);
dlmwrite('../Budget/Budget.txt', Z');
end

